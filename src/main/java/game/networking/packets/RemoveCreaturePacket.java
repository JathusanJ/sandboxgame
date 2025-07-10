package game.networking.packets;

import io.netty.buffer.ByteBuf;

public class RemoveCreaturePacket extends Packet {
    public int networkId;


    public RemoveCreaturePacket(int networkID) {
        this.networkId = networkID;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(networkId);
    }
}
