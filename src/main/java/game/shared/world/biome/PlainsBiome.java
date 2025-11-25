package game.shared.world.biome;

import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;
import game.shared.world.chunk.ChunkProxy;

import static game.shared.world.generators.DefaultWorldGenerator.addTrees;
import static game.shared.world.generators.DefaultWorldGenerator.spreadBlocks;

public class PlainsBiome extends Biome {
    @Override
    public void placeSurfaceBlocks(ChunkProxy chunkProxy, int chunkX, int chunkZ, int blockLocalX, int blockLocalY, int blockLocalZ) {
        Block blockAtPosition = chunkProxy.getRelative(blockLocalX,blockLocalY,blockLocalZ);
        Block blockAbove = chunkProxy.getRelative(blockLocalX,blockLocalY + 1,blockLocalZ);
        Block block2Above = chunkProxy.getRelative(blockLocalX, blockLocalY + 2, blockLocalZ);
        Block block3Above = chunkProxy.getRelative(blockLocalX, blockLocalY + 3, blockLocalZ);
        Block block4Above = chunkProxy.getRelative(blockLocalX,blockLocalY + 4,blockLocalZ);

        if(block4Above == Blocks.GRASS) {
            return;
        } else if(block3Above == Blocks.GRASS) {
            chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.DIRT);
        } else if(block2Above == Blocks.GRASS) {
            chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.DIRT);
        } else if(blockAbove == Blocks.GRASS) {
            chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.DIRT);
        } else if(blockAbove == null && blockLocalY >= 62 && blockAtPosition == Blocks.STONE) {
            chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.GRASS);
        }

        if(block3Above != Blocks.SAND) {
            if(block2Above == Blocks.SAND) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            } else if(blockAbove == Blocks.SAND) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            } else if((blockAbove == Blocks.WATER || blockAbove == null) && blockLocalY < 62 && blockAtPosition == Blocks.STONE) {
                chunkProxy.setRelative(blockLocalX, blockLocalY, blockLocalZ, Blocks.SAND);
            }
        }
    }

    @Override
    public void placeFeatures(ChunkProxy chunkProxy, int chunkX, int chunkZ, int localX, int localZ) {
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.RED_TULIP, 2000, 3, 10, 10);
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.ORANGE_TULIP, 2000, 3, 10, 10);
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.YELLOW_TULIP, 2000, 3, 10, 10);
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.SHORT_GRASS, 200, 3, 10, 10);
        spreadBlocks(chunkProxy, chunkX, chunkZ, localX, localZ, Blocks.PUMPKIN, 48000, 3, 10, 10);
        addTrees(chunkProxy, chunkX, chunkZ, localX, localZ, 1000, Blocks.OAK_LOG, Blocks.OAK_LEAVES);
        addTrees(chunkProxy, chunkX, chunkZ, localX, localZ, 2000, Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES);
    }
}
