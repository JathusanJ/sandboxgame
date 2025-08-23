package game.shared.multiplayer.packets;

import game.shared.world.creature.Player;
import game.shared.multiplayer.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class SetInventorySlotContentPacket extends Packet {
    public int slotId;
    public Player player;

    public SetInventorySlotContentPacket(Player player, int slotId) {
        this.slotId = slotId;
        this.player = player;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(this.slotId);
        ByteBufPacketDecoder.writeString(buffer, this.player.inventory[slotId].getItem().id);
        buffer.writeByte(this.player.inventory[slotId].amount);
    }
}
