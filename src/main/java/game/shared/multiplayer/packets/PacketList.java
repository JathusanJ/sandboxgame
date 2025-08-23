package game.shared.multiplayer.packets;

import java.util.ArrayList;
import java.util.HashMap;

public class PacketList {
    private static ArrayList<Class<? extends Packet>> idToPacket = new ArrayList<>();
    private static HashMap<Class<? extends Packet>, Integer> packetToId = new HashMap<>();

    public static void setup() {
        register(LoginAttemptPacket.class);
        register(LoginResultPacket.class);
        register(ChatMessagePacket.class);
        register(DisconnectPacket.class);
        register(PositionRotationPacket.class);
        register(ChunkDataPacket.class);
        register(ChunkReceivedPacket.class);
        register(SetBlockPacket.class);
        register(UseItemPacket.class);
        register(SetHotbarSlotPacket.class);
        register(SpawnCreaturePacket.class);
        register(RemoveCreaturePacket.class);
        register(CreatureMovePacket.class);
        register(SetInventorySlotContentPacket.class);
        register(DropItemPacket.class);
    }

    public static void register(Class<? extends Packet> packetClass) {
        idToPacket.add(packetClass);
        packetToId.put(packetClass, idToPacket.indexOf(packetClass));
    }

    public static int getIdOf(Class<? extends Packet> packet) {
        return packetToId.getOrDefault(packet, -1);
    }

    public static Class<? extends Packet> getPacketOf(int packetId) {
        if(packetId >= idToPacket.size()) {
            return null;
        }
        return idToPacket.get(packetId);
    }
}
