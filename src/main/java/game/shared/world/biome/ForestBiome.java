package game.shared.world.biome;

import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.ChunkProxy;

import static game.shared.world.generators.DefaultWorldGenerator.addTrees;
import static game.shared.world.generators.DefaultWorldGenerator.spreadBlocks;

public class ForestBiome extends PlainsBiome {
    @Override
    public void placeFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ, int localX, int localZ) {
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.SHORT_GRASS, 100, 3, 10, 10);
        addTrees(chunkProxy, chunkX, chunkZ, localX, localZ, 70, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        addTrees(chunkProxy, chunkX, chunkZ, localX, localZ, 150, Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
    }
}
