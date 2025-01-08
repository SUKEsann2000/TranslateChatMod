package com.translate.translatechat;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class Config {
        public static Map<String,String> defaultConfig = new HashMap<>();
        private static final Gson gson = new Gson();

        public static void setDefaultConfig(){
                defaultConfig.put("fetchURL","https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?");
                defaultConfig.put("fetchTextType","text=");
                defaultConfig.put("fetchTargetType","target=");
                defaultConfig.put("debug","false");
                defaultConfig.put("fetchKey","text");
                defaultConfig.put("playerNameIndexOf",">");
        }

        public static void addConfig(String servername, Map<String,String> config){
                JsonObject json = new JsonObject();
                json.add(servername,gson.toJsonTree(config));
                
                String jsonString = gson.toJson(json);
                writeConfigToFile(jsonString);
        }

        private static String getConfigPath(){
                Path configDir = FMLPaths.CONFIGDIR.get();
                return configDir.toString() + "/translatechat.json";
        }

        private static void writeConfigToFile(String jsonString){
                Path path = Paths.get(getConfigPath());
                try{
                        Files.createDirectories(path.getParent());
                        Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException e){
                        e.printStackTrace();
                }
        }

        public static JsonObject loadConfigFile(){
                Path path= Paths.get(getConfigPath());
                try{
                        String jsonString = new String(Files.readAllBytes(path));
                        return JsonParser.parseString(jsonString).getAsJsonObject();
                } catch (IOException | JsonSyntaxException e){
                        e.printStackTrace();
                        return null;
                }
        }

        public static String loadConfig(JsonObject configJson,String servername,String key){
                if (configJson == null) return null;
                Debug.debugConsole("configJson: " + configJson);
                Debug.debugConsole("servername: " + servername);
                Debug.debugConsole("key: " + key);

                
                JsonObject serverObject = configJson.getAsJsonObject(servername);

                if(serverObject == null) return null;
                JsonElement value = serverObject.get(key);
                if(value == null) {
                        if ("general".equals(servername)) return null;
                        return loadConfig(configJson, "general", key);
                }
                return value.getAsString();
        }
}