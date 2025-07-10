package engine.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {
    public int sourceId;
    public int bufferId;
    public Vector3f position;

    protected SoundSource() {
        this.sourceId = alGenSources();
    }

    protected SoundSource(int bufferId) {
        this();
        this.setBuffer(bufferId);
    }

    public void setBuffer(int bufferId) {
        this.stop();
        this.bufferId = bufferId;
        alSourcei(this.sourceId, AL_BUFFER, this.bufferId);
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        alSource3f(this.sourceId, AL_POSITION, this.position.x, this.position.y, this.position.z);
    }

    public void play() {
        alSourcePlay(this.sourceId);
    }

    public void pause() {
        alSourcePause(this.sourceId);
    }

    public void stop() {
        alSourceStop(this.sourceId);
    }

    public boolean isStopped() {
        return this.getState() == AL_STOPPED;
    }

    public int getState() {
        return alGetSourcei(AL_SOURCE_STATE, this.sourceId);
    }

    public void delete() {
        this.stop();
        alDeleteSources(this.sourceId);
    }
}
