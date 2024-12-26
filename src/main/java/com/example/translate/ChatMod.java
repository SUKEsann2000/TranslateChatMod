package com.example.translate;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import java.util.concurrent.CompletableFuture;

@Mod("chatmod")
public class ChatMod {

    public ChatMod() {
        // イベントを登録
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // システムメッセージは無視
        if (event.isSystem()) return;
        if (Minecraft.getInstance().player == null) return;

        // チャットメッセージの初期内容を取得
        String originalMessage = event.getMessage().getString();
        System.out.println("Received Message: " + originalMessage);

        // originalMessageからプレイヤー名などを除去（スペース以降の部分を抽出）
        String messageToTranslate = originalMessage.substring(originalMessage.indexOf(" ") + 1);

        // イベントをキャンセル
        event.setCanceled(true);

        // 非同期タスクで翻訳を実行
        CompletableFuture.supplyAsync(() -> {
            // 言語を取得して翻訳を実行
            String language = GetLanguage.main();
            return FetchJson.main(messageToTranslate, language);
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

}
