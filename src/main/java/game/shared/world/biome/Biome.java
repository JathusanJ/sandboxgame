package game.shared.world.biome;

import game.shared.world.chunk.ChunkProxy;

public abstract class Biome {
    public String id;

    public void setId(String id) {
        this.id = id;
    }

    public abstract void placeSurfaceBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalY, int blockLocalZ);

    public abstract void placeFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalZ);

    public String getId() {
        return this.id;
    }
}
