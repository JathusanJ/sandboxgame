package game.shared.multiplayer.packets;

import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;

public class PositionRotationPacket extends Packet {
    public Vector3f position;
    public float yaw;
    public float pitch;

    public PositionRotationPacket(Vector3f position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeFloat(this.position.x);
        buffer.writeFloat(this.position.y);
        buffer.writeFloat(this.position.z);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
    }
}
