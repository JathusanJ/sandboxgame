package game.networking.packets;

import io.netty.buffer.ByteBuf;

public class DropItemPacket extends Packet {
    public int slot;
    public int amount;

    public DropItemPacket(int slot, int amount) {
        this.slot = slot;
        this.amount = amount;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.slot);
        buffer.writeByte(this.amount);
    }
}
