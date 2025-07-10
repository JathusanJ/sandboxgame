package game.networking.packets;

import game.networking.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class ChatMessagePacket extends Packet {
    public String content;

    public ChatMessagePacket(String content) {
        this.content = content;
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufPacketDecoder.writeString(buffer, this.content);
    }

    public static ChatMessagePacket read(ByteBuf buffer) {
        return new ChatMessagePacket(ByteBufPacketDecoder.readString(buffer));
    }
}
