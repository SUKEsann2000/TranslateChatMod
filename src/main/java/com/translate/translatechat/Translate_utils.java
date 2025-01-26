package com.translate.translatechat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.player.Player;

public class Translate_utils {
        public static String getServerIp(String serverip) {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData serverData = minecraft.getCurrentServer();
        if (serverData != null) {
            Debug.debugConsole(serverip);
            return serverData.ip;
        }
        return "general";
    }

    public static String[] getPlayers(String serverip) {
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
}
