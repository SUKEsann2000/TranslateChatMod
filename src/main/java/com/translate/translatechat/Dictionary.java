package com.translate.translatechat;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Dictionary {
    private static final Map<String, String> dictionary = new HashMap<>();

    /**
     * Get the in-memory dictionary map.
     */
    public static Map<String, String> getDictionary() {
        return dictionary;
    }

    /**
     * Get the path to the dictionary file.
     */
    private static Path getDictionaryPath() {
        return FMLPaths.CONFIGDIR.get().resolve("translatechat_dictionary.json");
    }

    /**
     * Load the dictionary file as a JsonObject.
     */
    public static JsonObject loadDictionaryFile() {
        Path path = getDictionaryPath();
        try {
            if (Files.exists(path)) {
                String jsonString = Files.readString(path, StandardCharsets.UTF_8);
                return JsonParser.parseString(jsonString).getAsJsonObject();
            }
        } catch (IOException e) {
            System.err.println("Failed to read the dictionary file: " + path);
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid JSON syntax in dictionary file: " + path);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load the dictionary into memory from the JSON file.
     */
    public static void loadDictionary() {
        JsonObject json = loadDictionaryFile();
        if (json == null) return;

        dictionary.clear(); // Clear the existing dictionary before loading
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            dictionary.put(entry.getKey(), entry.getValue().getAsString());
        }
    }

    /**
     * Replace words in the input text based on the dictionary.
     *
     * @param text The input text to process.
     * @return The text after applying dictionary replacements.
     */
    public static String changeToDic(String text) {
        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            //text = text.replaceAll("(?i)" + entry.getKey(), entry.getValue());
            text = ReplaceText.main(text, entry.getValue(), entry.getKey());
        }
        return text;
    }

    /**
     * Create the dictionary file with default content if it does not exist.
     */
    public static void writeFirstDic() {
        Path path = getDictionaryPath();
        String defaultContent = "{\"example\":\"test\"}";

        try {
            Files.createDirectories(path.getParent()); // Ensure the parent directory exists
            if (Files.notExists(path)) {
                Files.writeString(path, defaultContent, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
                System.out.println("Default dictionary file created: " + path);
            }
        } catch (IOException e) {
            System.err.println("Failed to create the default dictionary file: " + path);
            e.printStackTrace();
        }
    }
}
