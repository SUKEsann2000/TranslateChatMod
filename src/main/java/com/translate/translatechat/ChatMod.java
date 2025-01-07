package com.translate.translatechat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.google.gson.JsonObject;

import net.minecraft.client.multiplayer.ServerData;

@Mod("chatmod")
public class ChatMod {
    public String fetchTextType;
    public String fetchURL;
    public String fetchTargetType;
    public Boolean debug;
    public String fetchKey;
    public String playerNameIndexOf;
    
    private static JsonObject config = new JsonObject();
    
    private static String serverip = "general";

    public ChatMod() {
        // イベントを登録
        MinecraftForge.EVENT_BUS.register(this);

        // コンフィグを登録
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // システムメッセージは無視
        // ここから
        // if (event.isSystem()) return;
        if (Minecraft.getInstance().player == null)
            return;

        // チャットメッセージの初期内容を取得
        String originalMessage = event.getMessage().getString();
        Debug.debugConsole("Received Message: " + originalMessage);

        // originalMessageからプレイヤー名などを除去（スペース以降の部分を抽出）
        if (originalMessage.indexOf(playerNameIndexOf) == -1) {
            Debug.debugConsole("No player index inriessage");
            return;
        } ;
        String messageFrom =
                originalMessage.substring(0, originalMessage.indexOf(playerNameIndexOf));
        String[] players = getPlayers();
        boolean found = false;
        for (String playerName : players) {
            if (messageFrom.contains(playerName)) {
                found = true;
                break;
            }
        }
        if (!found) {
            Debug.debugConsole("Message not from player");
            return;
        }
        // ここまでが例外をreturnするコード
        String messageToTranslate =
                originalMessage.substring(originalMessage.indexOf(playerNameIndexOf) + 1);

        // イベントをキャンセル
        event.setCanceled(true);

        // 非同期タスクで翻訳を実行
        CompletableFuture.supplyAsync(() -> {
            // 言語を取得して翻訳を実行
            String language = GetLanguage.main();
            return FetchJson.main(messageToTranslate, language, fetchURL, fetchTextType,
                    fetchTargetType, fetchKey);
        }).thenAccept(translateText -> {
            // メインスレッドで翻訳後のメッセージを表示
            Minecraft.getInstance().execute(() -> {
                String translatedMessage = originalMessage + "   " + translateText;
                Minecraft.getInstance().player
                        .sendSystemMessage(Component.literal(translatedMessage));
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

    @SubscribeEvent
    public void onClientLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        serverip = getServerIp();
        if(serverip == null) {
            return;
        }
        debug = Boolean.parseBoolean(Config.loadConfig(config, serverip, "debug"));
        if(debug == null | debug == false) {
            debug = Boolean.parseBoolean(Config.loadConfig(config, "general", "debug"));
            return;
        }
        fetchURL = Config.loadConfig(config, serverip, "fetchURL");
        fetchTextType = Config.loadConfig(config, serverip, "fetchTextType");
        fetchTargetType = Config.loadConfig(config, serverip, "fetchTargetType");
        fetchKey = Config.loadConfig(config, serverip, "fetchKey");
        playerNameIndexOf = Config.loadConfig(config, serverip, "playerNameIndexOf");
    }

    @SubscribeEvent
    public void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        serverip = "general";

        debug = Boolean.parseBoolean(Config.loadConfig(config, serverip, "debug"));
        fetchURL = Config.loadConfig(config, serverip, "fetchURL");
        fetchTextType = Config.loadConfig(config, serverip, "fetchTextType");
        fetchTargetType = Config.loadConfig(config, serverip, "fetchTargetType");
        fetchKey = Config.loadConfig(config, serverip, "fetchKey");
        playerNameIndexOf = Config.loadConfig(config, serverip, "playerNameIndexOf");
    }

    private static String[] getPlayers() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : level.players()) {
                playerNames.add(player.getName().getString());
            }
            return playerNames.toArray(new String[0]);
        }
        return new String[0];
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        config = Config.loadConfigFile();
        if (config == null) {
            Config.setDefaultConfig();
        } ;
        fetchURL = Config.loadConfig(config, "general", "fetchURL");
        fetchTextType = Config.loadConfig(config, "general", "fetchTextType");
        fetchTargetType = Config.loadConfig(config, "general", "fetchTargetType");
        fetchKey = Config.loadConfig(config, "general", "fetchKey");
        playerNameIndexOf = Config.loadConfig(config, "general", "playerNameIndexOf");
        debug = Boolean.parseBoolean(Config.loadConfig(config, "general", "debug"));

        Debug.onLoad(debug);
        Debug.debugConsole("Config loaded!! DebugMode now!");
        Debug.debugConsole("fetchURL: " + fetchURL);
        Debug.debugConsole("fetchTextType: " + fetchTextType);
        Debug.debugConsole("fetchTargetType: " + fetchTargetType);
        Debug.debugConsole("fetchKey: " + fetchKey);
        Debug.debugConsole("playerNameIndexOf: " + playerNameIndexOf);
    }

    private static String getServerIp() {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData serverData = minecraft.getCurrentServer();
        if (serverData != null) {
            System.out.println(serverData.ip);
            return serverData.ip;
        }
        return "general";
    }
}
