package game.networking.packets;

import io.netty.buffer.ByteBuf;
import org.joml.Vector2i;

public class RequestChunkPacket extends Packet {
    public Vector2i chunkPosition;

    public RequestChunkPacket(Vector2i position) {
        this.chunkPosition = position;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.chunkPosition.x);
        buffer.writeInt(this.chunkPosition.y);
    }
}
