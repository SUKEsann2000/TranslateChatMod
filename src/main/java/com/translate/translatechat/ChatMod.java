package com.translate.translatechat;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

/*
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;
 */

import java.util.concurrent.CompletableFuture;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.google.gson.JsonObject;

/*
import net.minecraft.client.multiplayer.ServerData;
 */

import java.util.HashMap;
import java.util.Map;

@Mod("chatmod")
public class ChatMod {
    private static String fetchTextType;
    private static String fetchURL;
    private static String fetchTargetType;
    private static Boolean debug;
    private static String fetchKey;
    private static String playerNameIndexOf;
    private static Boolean enable;
    private static Boolean enableDictionary;

    private Map<String, String> defaultConfig = new HashMap<>();


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

        if (!enable)
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
        String[] players = Translate_utils.getPlayers(serverip);
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

        // 翻訳をDictionaryに照らし合わせる
        String changedMessageToTranslate = Dictionary.changeToDic(messageToTranslate);
        Debug.debugConsole("Changed Message: " + changedMessageToTranslate);

        // イベントをキャンセル
        event.setCanceled(true);

        // 非同期タスクで翻訳を実行
        CompletableFuture.supplyAsync(() -> {
            // 言語を取得して翻訳を実行
            String language = GetLanguage.main();
            return FetchJson.main(changedMessageToTranslate, language, fetchURL, fetchTextType,
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
        Debug.debugConsole("onClientLoggingIn");
        changeSettings();
    }

    @SubscribeEvent
    public void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        Debug.debugConsole("onClientLoggingOut");
        changeSettings();
    }


    private void onCommonSetup(FMLCommonSetupEvent event) {
        config = Config.loadConfigFile();
        Config.setDefaultConfig();
        defaultConfig = Config.getDefaultConfig();
        if (config == null) {
            Config.addConfig("general", Config.getDefaultConfig());
            config = Config.loadConfigFile();
        }
        changeSettings();

        Debug.onLoad(debug);
        Debug.debugConsole("Config loaded!! DebugMode now!");
        Debug.debugConsole("enable: " + enable);
        Debug.debugConsole("fetchURL: " + fetchURL);
        Debug.debugConsole("fetchTextType: " + fetchTextType);
        Debug.debugConsole("fetchTargetType: " + fetchTargetType);
        Debug.debugConsole("fetchKey: " + fetchKey);
        Debug.debugConsole("playerNameIndexOf: " + playerNameIndexOf);
        Debug.debugConsole("enableDictionary: " + enableDictionary);
        if (enableDictionary) {
            Dictionary.writeFirstDic();
            Dictionary.loadDictionary();
        } ;
    }
    public static void changeSettings() {
        serverip = Translate_utils.getServerIp(serverip);
        Debug.debugConsole("serverip: " + serverip);
        if (serverip == null) {
            return;
        }
        Map<String,String> defaultConfig = Config.getDefaultConfig();

        for (String key : defaultConfig.keySet()) {
            String value = Config.loadConfig(config, serverip, key);
            Debug.debugConsole("key_value: " + key + "_" + value);
            if (value == null) {
                value = Config.loadConfig(config, "general", key);
                if (value == null) {
                    continue;
                }
            }

            switch (key) {
                case "enable":
                    enable = Boolean.parseBoolean(value);
                    Debug.debugConsole("enable: " + enable);
                    break;
                case "debug":
                    debug = Boolean.parseBoolean(value);
                    Debug.debugConsole("debug: " + debug);
                    break;
                case "fetchURL":
                    fetchURL = value;
                    Debug.debugConsole("fetchURL: " + fetchURL);
                    break;
                case "fetchTextType":
                    fetchTextType = value;
                    Debug.debugConsole("fetchTextType: " + fetchTextType);
                    break;
                case "fetchTargetType":
                    fetchTargetType = value;
                    Debug.debugConsole("fetchTargetType: " + fetchTargetType);
                    break;
                case "fetchKey":
                    fetchKey = value;
                    Debug.debugConsole("fetchKey: " + fetchKey);
                    break;
                case "playerNameIndexOf":
                    playerNameIndexOf = value;
                    Debug.debugConsole("playerNameIndexOf: " + playerNameIndexOf);
                    break;
                case "enableDictionary":
                    enableDictionary = Boolean.parseBoolean(value);
                    Debug.debugConsole("enableDictionary: " + enableDictionary);
                    break;
            }
        }
    }
}
