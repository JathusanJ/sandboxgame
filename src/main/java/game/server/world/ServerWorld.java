package game.server.world;

import game.shared.world.World;
import game.shared.world.chunk.Chunk;
import game.shared.world.blocks.Block;
import game.shared.world.creature.Creature;
import game.server.GameServer;
import game.shared.multiplayer.packets.RemoveCreaturePacket;
import game.shared.multiplayer.packets.SetBlockPacket;
import game.shared.multiplayer.packets.SpawnCreaturePacket;

import java.io.File;

public class ServerWorld extends World {
    public GameServer server;
    public int ticksUntilAutosave = 10 * 60 * 20;

    public ServerWorld(File worldFolder, GameServer server) {
        this.worldFolder = worldFolder;
        this.chunksFolder = new File(this.worldFolder, "chunks");
        this.chunksFolder.mkdirs();
        this.loadWorldInfo();

        this.worldGenerator = this.worldType.getGenerator(this.seed);

        this.server = server;
        this.spawnLoadTicket.radius = server.worldSize;
    }

    public ServerWorld(String name, int seed, WorldType worldType, File worldFolder, GameServer server) {
        this.name = name;
        this.seed = seed;
        this.worldType = worldType;
        this.worldFolder = worldFolder;
        this.chunksFolder = new File(this.worldFolder, "chunks");
        this.chunksFolder.mkdirs();

        this.worldGenerator = this.worldType.getGenerator(this.seed);

        this.server = server;
        this.spawnLoadTicket.radius = server.worldSize;
    }

    @Override
    public Chunk createChunk(int x, int y) {
        return new ServerChunk(x, y, this);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Block block) {
        if(super.setBlock(x,y,z, block)) {
            SetBlockPacket packet = new SetBlockPacket(x, y, z, block);
            this.server.sendPacketToAll(packet);
            return true;
        }

        return false;
    }

    @Override
    public void spawnCreature(Creature creature) {
        super.spawnCreature(creature);
        creature.networkId = this.server.getNextNetworkId();

        for(int i = 0; i < this.server.players.size(); i++) {
            ServerPlayer player = this.server.players.get(i);
            if(player != creature) {
                player.sendPacket(new SpawnCreaturePacket(creature));
            }
        }
    }

    @Override
    public void onCreatureRemoved(Creature creature) {
        RemoveCreaturePacket packet = new RemoveCreaturePacket(creature.networkId);
        this.server.sendPacketToAll(packet);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksUntilAutosave = this.ticksUntilAutosave - 1;

        if(this.ticksUntilAutosave == (3 * 60 * 20)) {
            this.server.sendServerMessage("Autosave in 3 minutes");
        } else if(this.ticksUntilAutosave == (30 * 20)) {
            this.server.sendServerMessage("Autosave in 30 seconds");
        } else if(this.ticksUntilAutosave == (5 * 20)) {
            this.server.sendServerMessage("Autosave in 5 seconds");
        } else if(this.ticksUntilAutosave == 0) {
            this.server.sendServerMessage("Saving world");
            this.save();
            this.server.sendServerMessage("World saved");
            this.server.sendServerMessage("Next autosave in 10 minutes");
            this.ticksUntilAutosave = 10 * 60 * 20;
        }
    }
}
