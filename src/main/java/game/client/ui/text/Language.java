package game.client.ui.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

public class Language {
    private static HashMap<String, String> currentLanguage;

    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void load(String languageId){
        try {
            currentLanguage = gson.fromJson(new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/language/" + languageId + ".json").readAllBytes()), HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't load language file: " + e);
        }
    }

    public static String translate(String key){
        return currentLanguage.getOrDefault(key, key);
    }
}