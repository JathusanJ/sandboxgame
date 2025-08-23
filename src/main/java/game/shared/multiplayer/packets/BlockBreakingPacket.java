package game.shared.multiplayer.packets;

import io.netty.buffer.ByteBuf;
import org.joml.Vector3i;

public class BlockBreakingPacket extends Packet {
    public Vector3i blockPosition;
    public State type;

    public BlockBreakingPacket(State type) {
        if(type == State.CLIENT_START) {
            throw new IllegalArgumentException("Wrong constructor for CLIENT_START BlockBreakingPacket");
        }
        this.type = type;
    }

    public BlockBreakingPacket(Vector3i blockPosition) {
        this.type = State.CLIENT_START;
        this.blockPosition = blockPosition;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.type.ordinal());
        if(this.type == State.CLIENT_START) {
            buffer.writeInt(this.blockPosition.x);
            buffer.writeInt(this.blockPosition.y);
            buffer.writeInt(this.blockPosition.z);
        }
    }

    public enum State {
        CLIENT_START,
        CLIENT_STOP,
        SERVER_STOP
    }
}
