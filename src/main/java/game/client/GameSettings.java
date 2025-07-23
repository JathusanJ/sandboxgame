package game.client;

import com.google.gson.stream.JsonReader;
import game.logic.util.json.WrappedJsonObject;

import java.io.*;

public class GameSettings {
    public boolean vsync = true;
    public int renderDistance = 8;
    public File settingsFile;

    public GameSettings(File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public void save() {
        WrappedJsonObject json = new WrappedJsonObject();
        json.put("vsync", this.vsync);
        json.put("renderDistance", this.renderDistance);

        try {
            if(!this.settingsFile.exists()) {
                if(!this.settingsFile.createNewFile()) {
                    throw new IllegalStateException("Couldn't create settings.json");
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(this.settingsFile);
            fileOutputStream.write(json.toElement().toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write settings.json", e);
        }
    }

    public void load() {
        if(!this.settingsFile.exists()) return;

        WrappedJsonObject json;

        try {
            FileInputStream fileInputStream = new FileInputStream(this.settingsFile);
            json = WrappedJsonObject.read(new JsonReader(new StringReader(new String(fileInputStream.readAllBytes()))));
            fileInputStream.close();
        } catch(Exception e) {
            SandboxGame.getInstance().logger.error("Couldn't read settings.json", e);
            return;
        }

        this.vsync = json.getBoolean("vsync");
        this.renderDistance = json.getInt("renderDistance");
    }
}
