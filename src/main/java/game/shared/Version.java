package game.shared;

import com.google.gson.GsonBuilder;

import java.io.IOException;

public record Version(String versionId, String versionName, String git) {
    public static Version GAME_VERSION;

    public static void load() {
        try {
            GAME_VERSION = new GsonBuilder().create().fromJson(new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("version.json").readAllBytes()) , Version.class);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load game version data", e);
        }
    }
}
