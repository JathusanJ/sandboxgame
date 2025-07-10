package game.networking.packets;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
    public abstract void write(ByteBuf buffer);
}
