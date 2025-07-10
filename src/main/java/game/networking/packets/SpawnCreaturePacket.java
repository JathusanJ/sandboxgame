package game.networking.packets;

import game.logic.world.creature.Creature;
import game.logic.world.creature.Creatures;
import game.networking.ByteBufPacketDecoder;
import io.netty.buffer.ByteBuf;

public class SpawnCreaturePacket extends Packet {
    public Creature creature;

    public SpawnCreaturePacket(Creature creature) {
        this.creature = creature;
    }

    @Override
    public void write(ByteBuf buffer) {
        ByteBufPacketDecoder.writeString(buffer, Creatures.getIdFor(creature));
        buffer.writeInt(creature.networkId);
        this.creature.writeSpawnPacket(buffer);
    }
}
