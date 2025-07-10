package engine.sound;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.openal.ALC10.*;

// https://web.archive.org/web/20250111185349/https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter22/chapter22.html
public class Sounds {
    public static long device;
    public static long context;
    public static HashMap<String, Sound> sounds = new HashMap<>();
    private static ArrayList<SoundSource> soundSources = new ArrayList<>();

    public static void initialize(){
        device = alcOpenDevice((ByteBuffer) null);

        ALCCapabilities deviceCapabilities = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCapabilities);
    }

    public static Sound getSound(String filepath) {
        if(!sounds.containsKey(filepath)) {
            sounds.put(filepath, new Sound(filepath));
        }

        return sounds.get(filepath);
    }

    public static SoundSource createSource() {
        return new SoundSource();
    }

    public static void playSound(String filepath) {
        SoundSource foundSource = null;

        for (int i = 0; i < soundSources.size(); i++) {
            SoundSource soundSource = soundSources.get(i);
            if(soundSource.isStopped()) {
                foundSource = soundSource;
                break;
            }
        }

        if(foundSource == null) {
            if(soundSources.size() >= 1024) {
                throw new IllegalStateException("There are too many sound sources");
            }
            foundSource = new SoundSource();
            soundSources.add(foundSource);
        }

        foundSource.setBuffer(getSound(filepath).bufferId);
        foundSource.play();
    }

    public static void unload() {
        for(SoundSource source : soundSources) {
            source.delete();
        }

        for(Sound sound : sounds.values()) {
            sound.delete();
        }

        alcDestroyContext(context);
        alcCloseDevice(device);
    }
}
