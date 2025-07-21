package game.logic.world;

import game.logic.world.chunk.Chunk;
import game.logic.world.blocks.Block;
import game.logic.world.chunk.ServerChunk;
import game.logic.world.creature.Creature;
import game.logic.world.creature.Player;
import game.networking.GameServer;
import game.logic.world.creature.ServerPlayer;
import game.networking.packets.SetBlockPacket;
import game.networking.packets.SpawnCreaturePacket;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

public class ServerWorld extends World {
    public GameServer server;
    private ArrayList<Vector2i> chunksToUnloadThisTick = new ArrayList<>();

    public ServerWorld(File worldFolder, GameServer server) {
        this.worldFolder = worldFolder;
        this.chunksFolder = new File(this.worldFolder, "chunks");
        this.chunksFolder.mkdirs();
        this.loadWorldInfo();

        this.worldGenerator = this.worldType.getGenerator(this.seed);

        this.server = server;
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
    }

    @Override
    public Chunk createChunk(int x, int y) {
        return new ServerChunk(x, y, this);
    }
}
