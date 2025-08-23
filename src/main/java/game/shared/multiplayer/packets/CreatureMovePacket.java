package game.shared.multiplayer.packets;

import game.shared.world.creature.Creature;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;

public class CreatureMovePacket extends Packet {
    public Vector3f position;
    public float yaw;
    public int networkId;

    public CreatureMovePacket(Creature creature) {
        this.position = creature.position;
        this.yaw = creature.yaw;
        this.networkId = creature.networkId;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.networkId);
        buffer.writeFloat(this.position.x);
        buffer.writeFloat(this.position.y);
        buffer.writeFloat(this.position.z);
        buffer.writeFloat(this.yaw);
    }
}
