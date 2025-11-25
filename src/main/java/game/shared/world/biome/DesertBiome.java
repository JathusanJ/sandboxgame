package game.shared.world.biome;

import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.ChunkProxy;

public class DesertBiome extends Biome {
    @Override
    public void placeSurfaceBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalY, int blockLocalZ) {
        Block blockAtPosition = chunkProxy.getRelative(blockLocalX,blockLocalY,blockLocalZ);
        Block blockAbove = chunkProxy.getRelative(blockLocalX,blockLocalY + 1,blockLocalZ);
        Block block2Above = chunkProxy.getRelative(blockLocalX, blockLocalY + 2, blockLocalZ);
        Block block3Above = chunkProxy.getRelative(blockLocalX, blockLocalY + 3, blockLocalZ);
        Block block4Above = chunkProxy.getRelative(blockLocalX, blockLocalY + 4, blockLocalZ);

        if(block4Above != Blocks.SAND) {
            if (block3Above == Blocks.SAND) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            } else if (block2Above == Blocks.SAND) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            } else if (blockAbove == Blocks.SAND) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            } else if ((blockAbove == Blocks.WATER || blockAbove == null) && blockAtPosition == Blocks.STONE) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            }
        }
    }

    @Override
    public void placeFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalZ) {

    }

}
