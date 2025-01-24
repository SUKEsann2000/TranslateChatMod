package com.translate.translatechat;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Dictionary {
    private static final Map<String, String> dictionary = new HashMap<>();

    public static Map<String, String> getDictionary() {
        return dictionary;
    }

    private static String getDictionaryPath() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        return configDir.resolve("translatechat_dictionary.json").toString();
    }

    public static JsonObject loadDictionaryFile() {
        Path path = Path.of(getDictionaryPath());
        try {
            String jsonString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadDictionary() {
        JsonObject json = loadDictionaryFile();
        if (json == null)
            return;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            dictionary.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    public static String changeToDic(String text) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            // text = text.replaceAll("(?i)"+entry.getKey(), entry.getValue());
            return ReplaceText.main(text, entry.getValue(), entry.getKey());
        }
        return text;
    }

    public static void writeFirstDic() {
        Path path = Paths.get(getDictionaryPath());
        String jsonString =
                JsonParser.parseString("[\"\\glhf\"\":\\\"\\\\example\\\"\\\"]").toString();
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(Paths.get(getDictionaryPath()))) {
                Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
