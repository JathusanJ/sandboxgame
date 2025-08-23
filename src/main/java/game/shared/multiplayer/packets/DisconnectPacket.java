package game.shared.multiplayer.packets;

import game.shared.multiplayer.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class DisconnectPacket extends Packet {
    public String reason;

    // For client use
    public DisconnectPacket() {

    }

    // For server use
    public DisconnectPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public void write(ByteBuf buffer) {
        if(this.reason != null) {
            ByteBufPacketDecoder.writeString(buffer, this.reason);
        }
    }
}
