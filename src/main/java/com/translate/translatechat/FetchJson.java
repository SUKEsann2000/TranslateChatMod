package com.translate.translatechat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FetchJson {
    public static String main(String chatText,String target) {
        // HTTPクライアントを作成 (リダイレクトの追跡)
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)  // リダイレクトを常に追跡
            .build();

        // リクエストを作成
        String encodedText = URLEncoder.encode(chatText,StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://script.google.com/macros/s/AKfycbxd0Z5iavmXxdxdtn71VYftLvIBzCjmE2NuxUSZw24z-JuYjuOf-FO3B922MBW3D_Y/exec?text=" + encodedText + "&target=" + target))  // リダイレクトをテストするURL
            .build();

        try {
            // レスポンスを非同期で取得
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // ステータスコードが200ならば、JSONデータを処理
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                System.out.println(responseBody);
                // JSONデータをパース（org.json）
                //JSONObject jsonObject = new JSONObject(responseBody);
                //System.out.println("Fetched JSON: " + jsonObject.toString(2));  // Pretty print
                //return jsonObject.toString(2);
                Map<String, String> jsonMap = parseJson(responseBody);
                System.out.println("Fetched JSON: " + jsonMap.toString());
                //"text"を取り出し
                String translateText = jsonMap.get("text");
                System.out.println("Extracted text: " + translateText);

                return translateText;
            } else {
                System.out.println("Request failed with status: " + response.statusCode());
                return Integer.toString(response.statusCode()); //
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }
            // 手動でJSONをパースするメソッド
    private static Map<String, String> parseJson(String json) {
        Map<String, String> resultMap = new HashMap<>();

        // 波括弧を除去
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        // カンマで分割してキーと値のペアを処理
        String[] keyValuePairs = json.split(",");
        for (String pair : keyValuePairs) {
            // キーと値をコロンで分割
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                // キーと値の周りのクオーテーションを取り除く
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }
}
