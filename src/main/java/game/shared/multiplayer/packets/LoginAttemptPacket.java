package game.shared.multiplayer.packets;

import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.skin.Skin;
import io.netty.buffer.ByteBuf;

public class LoginAttemptPacket extends Packet {
    public String username;
    public Skin skin;

    public LoginAttemptPacket(String username, Skin skin) {
        this.username = username;
        this.skin = skin;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeInt(0);
        ByteBufPacketDecoder.writeString(buffer, this.username);
        ByteBufPacketDecoder.writeString(buffer, this.skin.id);
    }
}
