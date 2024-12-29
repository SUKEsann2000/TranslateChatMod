package com.translate.translatechat;

public class Debug {
    static boolean debugMode;
        public static void debugConsole(String message){
            if(debugMode){
            System.out.println(" [chatmod_debug] " + message);
            }
        }
        public static void onLoad(Boolean isDebug){
            debugMode = isDebug;
    }
}
