package com.translate.translatechat;

import net.minecraft.client.Minecraft;

public class GetLanguage {
    public static String main() {
        Minecraft minecraft = Minecraft.getInstance();

        // LanguageManager を使用して現在の言語を取得
        String currentLanguage = minecraft.getLanguageManager().getSelected().getCode();

        // 言語を確認
        Debug.debugConsole("現在の言語コード: " + currentLanguage);
        return currentLanguage;
    }
}


