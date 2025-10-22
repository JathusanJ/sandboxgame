package game.shared.world.blocks;

import game.shared.world.chunk.ChunkProxy;

public interface RandomTickable {
    void randomTick(ChunkProxy chunkProxy, int localX, int localY, int localZ, int worldX, int worldY, int worldZ);
}
