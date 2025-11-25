package game.shared.world.biome;

import game.shared.world.chunk.ChunkProxy;

public class NoBiome extends Biome {
    @Override
    public void placeSurfaceBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalY, int blockLocalZ) {

    }

    @Override
    public void placeFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalZ) {

    }
}
