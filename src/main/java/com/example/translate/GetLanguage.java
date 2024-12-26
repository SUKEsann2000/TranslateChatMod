package com.example.translate;

import net.minecraft.client.Minecraft;

public class GetLanguage {
    public static String main() {
        Minecraft minecraft = Minecraft.getInstance();

        // LanguageManager を使用して現在の言語を取得
        String currentLanguage = minecraft.getLanguageManager().getSelected().getCode();

        // 言語を確認
        System.out.println("現在の言語コード: " + currentLanguage);
        return currentLanguage;
    }
}


