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
    private static Map<String, String> defaultConfig = new HashMap<>();
    private static final Gson gson = new Gson();

    /*
     * Set the default configuration values.
     */
    public static void setDefaultConfig() {
        defaultConfig.put("enable", "true");
        defaultConfig.put("fetchURL", "https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?");
        defaultConfig.put("fetchTextType", "text=");
        defaultConfig.put("fetchTargetType", "target=");
        defaultConfig.put("debug", "false");
        defaultConfig.put("fetchKey", "text");
        defaultConfig.put("playerNameIndexOf", ">");
        defaultConfig.put("enableDictionary", "true");
        Debug.debugConsole(defaultConfig.toString());
    }

    /*
     * Get the default configuration values.
     */
    public static Map<String, String> getDefaultConfig() {
        return defaultConfig;
    }

    /*
     * Add a configuration to the config file.
     * @param servername 追加するサーバー名
     * @param config 追加するconfig（Map）
     */
    public static void addConfig(String servername, Map<String, String> config) {
        JsonObject json = new JsonObject();
        json.add(servername, gson.toJsonTree(config));

        String jsonString = gson.toJson(json);
        writeConfigToFile(jsonString);
    }

    /*
     * Get the path to the configuration file.
     */
    private static String getConfigPath() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        return configDir.resolve("translatechat_config.json").toString();
    }

    /*
     * Write the configuration to the configuration file.
     * @param jsonString 書き込むjson（String）
     */
    private static void writeConfigToFile(String jsonString) {
        Path path = Paths.get(getConfigPath());
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Load the configuration file as a JsonObject.
     * @return ファイルをJsonObjectとして
     */
    public static JsonObject loadConfigFile() {
        Path path = Paths.get(getConfigPath());
        try {
            String jsonString = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            return JsonParser.parseString(jsonString).getAsJsonObject();
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Load the configuration value from the configuration file.
     * @param configJson 読み込むconfig（JsonObject）
     * @param servername 読み込むサーバー名
     * @param key 読み込むキー
     * @return 読み込んだ値（String）
     */
    public static String loadConfig(JsonObject configJson, String servername, String key) {
        if (configJson == null) return null;
        Debug.debugConsole("configJson: " + configJson);
        Debug.debugConsole("servername: " + servername);
        Debug.debugConsole("key: " + key);

        JsonObject serverObject = configJson.getAsJsonObject(servername);

        if (serverObject == null) {
            Debug.debugConsole("serverObject is null");
            if ("general".equals(servername)) {
                setDefaultConfig();
                JsonObject json = new JsonObject();
                json.add("general", gson.toJsonTree(defaultConfig));
                Debug.debugConsole("json: " + json + "\nkey: " + key);
                writeConfigToFile(gson.toJson(json));
                return defaultConfig.getOrDefault(key, null);
            }
            return loadConfig(configJson, "general", key);
        }

        JsonElement value = serverObject.get(key);
        if (value == null) {
            Debug.debugConsole("value is null");
            if ("general".equals(servername)) {
                setDefaultConfig();
                JsonObject json = new JsonObject();
                json.add("general", gson.toJsonTree(defaultConfig));
                writeConfigToFile(gson.toJson(json));
                return defaultConfig.getOrDefault(key, null);
            }
            return loadConfig(configJson, "general", key);
        }

        return value.isJsonPrimitive() ? value.getAsString() : null;
    }
}
