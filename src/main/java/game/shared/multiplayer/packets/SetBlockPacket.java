package game.shared.multiplayer.packets;

import game.shared.world.blocks.Block;
import game.shared.multiplayer.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class SetBlockPacket extends Packet {
    public int x;
    public int y;
    public int z;
    public Block block;

    public SetBlockPacket(int x, int y, int z, Block block) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.y);
        buffer.writeInt(this.z);
        ByteBufPacketDecoder.writeString(buffer, this.block.getBlockId());
    }
}
