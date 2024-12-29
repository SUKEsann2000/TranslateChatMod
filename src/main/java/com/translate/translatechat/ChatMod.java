package com.translate.translatechat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.CompletableFuture;

import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;

@Mod("chatmod")
public class ChatMod {
    public String fetchTextType;
    public String fetchURL;
    public String fetchTargetType;
    public Boolean debug;
    public String fetchKey;

    public ChatMod() {
        // イベントを登録
        MinecraftForge.EVENT_BUS.register(this);

        // コンフィグを登録
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.COMMON_SPEC);
        
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // システムメッセージは無視
        //ここから
        //if (event.isSystem()) return;
        if (Minecraft.getInstance().player == null) return;

        // チャットメッセージの初期内容を取得
        String originalMessage = event.getMessage().getString();
        Debug.debugConsole("Received Message: " + originalMessage);

        // originalMessageからプレイヤー名などを除去（スペース以降の部分を抽出）
        if(originalMessage.indexOf(" ") == -1) {
            Debug.debugConsole("No space in message");
            return;
        };
        String messageFrom = originalMessage.substring(0,originalMessage.indexOf(" "));
        String[] players = getPlayers();
        boolean found = false;
        for (String playerName : players) {
            if(messageFrom.contains(playerName)) {
                found = true;
                break;
            }
        }
        if(!found) {
            Debug.debugConsole("Message not from player");
            return;
        }
        //ここまでが例外をreturnするコード
        String messageToTranslate = originalMessage.substring(originalMessage.indexOf(" ") + 1);

        // イベントをキャンセル
        event.setCanceled(true);

        // 非同期タスクで翻訳を実行
        CompletableFuture.supplyAsync(() -> {
            // 言語を取得して翻訳を実行
            String language = GetLanguage.main();
            return FetchJson.main(messageToTranslate, language,fetchURL,fetchTextType,fetchTargetType,fetchKey);
        }).thenAccept(translateText -> {
            // メインスレッドで翻訳後のメッセージを表示
            Minecraft.getInstance().execute(() -> {
                String translatedMessage = originalMessage + "   " + translateText;
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(translatedMessage));
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            Minecraft.getInstance().execute(() -> {
                String errorMessage = originalMessage + "   (Translation Error)";
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(errorMessage));
            });
            return null;
        });
    }
    private static String[] getPlayers(){
        ClientLevel level = Minecraft.getInstance().level;
        if(level != null) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : level.players()){
                playerNames.add(player.getName().getString());
            }
            return playerNames.toArray(new String[0]);
        }
        return new String[0];
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        fetchURL = Config.COMMON.fetchURL.get();
        fetchTextType = Config.COMMON.fetchTextType.get();
        fetchTargetType = Config.COMMON.fetchTargetType.get();
        fetchKey = Config.COMMON.fetchKey.get();
        debug = Config.COMMON.debug.get();

        Debug.onLoad(debug);
        Debug.debugConsole("Config loaded!! DebugMode now!");
        Debug.debugConsole("fetchURL: " + fetchURL);
        Debug.debugConsole("fetchTextType: " + fetchTextType);
        Debug.debugConsole("fetchTargetType: " + fetchTargetType);
        Debug.debugConsole("fetchKey: " + fetchKey);
    }
}
