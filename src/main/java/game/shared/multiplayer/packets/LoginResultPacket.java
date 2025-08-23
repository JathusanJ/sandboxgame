package game.shared.multiplayer.packets;

import game.server.GameServer;
import io.netty.buffer.ByteBuf;

public class LoginResultPacket extends Packet {
    public byte success;
    public GameServer server;

    public LoginResultPacket() {
        this.success = 0;
    }

    public LoginResultPacket(GameServer server) {
        this.server = server;
        this.success = 1;
    }

    @Override
    public void write(ByteBuf buffer) {
        buffer.writeByte(success);
        buffer.writeByte(this.server.world.spawnLoadTicket.radius);
    }
}
