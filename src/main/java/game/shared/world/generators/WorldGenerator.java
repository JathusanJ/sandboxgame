package game.shared.world.generators;

import game.shared.world.chunk.ChunkProxy;

public abstract class WorldGenerator {
    int seed;

    public abstract void generate(ChunkProxy chunkProxy, int chunkX, int chunkZ);

    public abstract void generateFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ);
}
