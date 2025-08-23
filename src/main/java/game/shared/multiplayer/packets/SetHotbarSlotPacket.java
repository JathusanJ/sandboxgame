package game.shared.multiplayer.packets;

import io.netty.buffer.ByteBuf;

public class SetHotbarSlotPacket extends Packet {
    public int slot;

    public SetHotbarSlotPacket(int slot) {
        this.slot = slot;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(slot);
    }
}
