package game.shared.multiplayer.packets;

import game.shared.world.creature.Player;
import game.server.world.ServerPlayer;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;

public abstract class PacketHandler {
    public HashMap<Class<? extends Packet>, HandlePacket> packetHandlers = new HashMap<>();

    public void readAndHandle(Player player, ByteBuf buffer) {
        byte packetId = buffer.readByte();

        if(packetId > 0 && player instanceof ServerPlayer serverPlayer && serverPlayer.playerProfile == null) {
            throw new IllegalStateException("Uninitialized player attempted to send packet that isn't a login packet");
        }

        @SuppressWarnings("unchecked")
        Class<Packet> packetClass = (Class<Packet>) PacketList.getPacketOf(packetId);
        if(packetClass == null) {
            throw new IllegalStateException("Attempted to read non existent packet id " + packetId);
        }

        HandlePacket handlePacket = packetHandlers.get(packetClass);
        if(handlePacket == null) {
            throw new IllegalStateException("Attempted to handle packet of id " + packetId + ", which doesn't have a packet handler");
        }
        handlePacket.readAndHandle(player, buffer);
    }

    public interface HandlePacket {
        void readAndHandle(Player sender, ByteBuf buffer);
    }
}
