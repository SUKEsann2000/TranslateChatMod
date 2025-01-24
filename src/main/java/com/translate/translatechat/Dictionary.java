package com.translate.translatechat;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Dictionary {
    private static final Map<String,String> dictionary = new HashMap<>();

    public static Map<String, String> getDictionary() {
        return dictionary;
    }

    private static String getDictionaryPath() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        return configDir.resolve("translatechat_dictionary.json").toString();
    }

    public static JsonObject loadDictionaryFile(){
        Path path = Path.of(getDictionaryPath());
        try {
            String jsonString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadDictionary(){
        JsonObject json = loadDictionaryFile();
        if(json == null) return;
        for(Map.Entry<String, JsonElement> entry : json.entrySet()){
            dictionary.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    public static String changeToDic(String text){
        for(Map.Entry<String, String> entry : dictionary.entrySet()){
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}
