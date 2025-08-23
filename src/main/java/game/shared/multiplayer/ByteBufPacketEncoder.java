package game.shared.multiplayer;

import game.shared.multiplayer.packets.Packet;
import game.shared.multiplayer.packets.PacketList;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ByteBufPacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf buffer) throws Exception {
        buffer.writeInt(0);
        buffer.writeByte(PacketList.getIdOf(packet.getClass()));
        packet.write(buffer);
        buffer.setInt(0, buffer.readableBytes() - 4);
    }
}
