package game.shared.multiplayer.packets;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
    public abstract void write(ByteBuf buffer);
}
