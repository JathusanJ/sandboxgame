package game.shared.multiplayer.packets;

import io.netty.buffer.ByteBuf;

public class ChunkRequestFailurePacket extends Packet {
    public FailureType failureType;
    public int chunkX;
    public int chunkY;

    public ChunkRequestFailurePacket(FailureType failureType, int chunkX, int chunkY) {
        this.failureType = failureType;
        this.chunkX = chunkX;
        this.chunkY = chunkY;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.failureType.ordinal());
        buffer.writeInt(chunkX);
        buffer.writeInt(chunkY);
    }

    public enum FailureType {
        CHUNK_NOT_LOADED_YET
    }
}
