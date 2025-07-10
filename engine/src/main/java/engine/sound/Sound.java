package engine.sound;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class Sound {
    public int bufferId;
    public int channels;
    public int sampleRate;

    protected Sound(String soundPath) {
        this.bufferId = alGenBuffers();

        STBVorbisInfo info = STBVorbisInfo.malloc();
        IntBuffer error = BufferUtils.createByteBuffer(4).asIntBuffer();

        byte[] data;
        try {
            data = Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/sounds/" + soundPath).readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();

        long result = STBVorbis.stb_vorbis_open_memory(buffer, error, null);
        STBVorbis.stb_vorbis_get_info(result, info);

        int size = STBVorbis.stb_vorbis_stream_length_in_samples(result) * info.channels();
        ShortBuffer toPCM = BufferUtils.createShortBuffer(size);

        STBVorbis.stb_vorbis_get_samples_short_interleaved(result, info.channels(), toPCM);
        STBVorbis.stb_vorbis_close(result);

        alBufferData(this.bufferId, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, toPCM, info.sample_rate());
        this.channels = info.channels();
        this.sampleRate = info.sample_rate();
        info.close();
    }

    public void delete() {
        alDeleteBuffers(this.bufferId);
    }
}
