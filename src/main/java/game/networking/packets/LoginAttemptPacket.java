package game.networking.packets;

import game.networking.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class LoginAttemptPacket extends Packet {
    public String username;

    public LoginAttemptPacket(String username) {
        this.username = username;
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufPacketDecoder.writeString(buffer, this.username);
    }
}
