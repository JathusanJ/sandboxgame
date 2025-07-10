package game.networking.packets;

import io.netty.buffer.ByteBuf;

public class UseItemPacket extends Packet {
    public int targetX;
    public int targetY;
    public int targetZ;
    public int normalX;
    public int normalY;
    public int normalZ;

    public UseItemPacket(int targetX, int targetY, int targetZ, int normalX, int normalY, int normalZ) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(targetX);
        buffer.writeInt(targetY);
        buffer.writeInt(targetZ);
        buffer.writeInt(normalX);
        buffer.writeInt(normalY);
        buffer.writeInt(normalZ);
    }
}
