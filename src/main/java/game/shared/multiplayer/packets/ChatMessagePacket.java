package game.shared.multiplayer.packets;

import game.shared.multiplayer.ByteBufPacketDecoder;
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
}
