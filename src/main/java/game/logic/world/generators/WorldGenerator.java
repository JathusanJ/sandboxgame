package game.logic.world.generators;

import game.logic.world.blocks.Block;
import game.logic.world.chunk.ChunkProxy;
import org.joml.Vector3i;

import java.util.HashMap;

public abstract class WorldGenerator {
    int seed;

    public abstract void generate(ChunkProxy chunkProxy, int chunkX, int chunkZ);

    public abstract void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ);
}
