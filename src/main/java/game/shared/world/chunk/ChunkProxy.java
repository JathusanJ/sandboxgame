package game.shared.world.chunk;

import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;

// Not the best way, but I want things to work at the moment
public class ChunkProxy {
    public Chunk chunk;

    public ChunkProxy(Chunk chunk) {
        this.chunk = chunk;
    }

    public void setRelative(int x, int y, int z, Block block) {
        if(y < 0 || y > 127) return;

        if(this.isRelativePositionInChunk(x,y,z)) {
            this.chunk.setBlockAtLocalizedPositionDirect(x, y, z, block);
        } else {
            this.chunk.world.setBlockNoRemesh(this.chunk.chunkPosition.x * 16 + x, y, this.chunk.chunkPosition.y * 16 + z, block);
        }
    }

    public void setRelativeIfAbsent(int x, int y, int z, Block block) {
        Block blockAtPosition = this.getRelative(x,y,z);
        if(blockAtPosition == null || blockAtPosition == Blocks.AIR) {
            this.setRelative(x,y,z, block);
        }
    }

    public Block getRelative(int x, int y, int z) {
        if(this.isRelativePositionInChunk(x,y,z)) {
            return this.chunk.getBlockAtLocalizedPositionDirect(x, y, z);
        }

        return this.chunk.world.getBlock(x + this.chunk.chunkPosition.x * 16, y, z + this.chunk.chunkPosition.y * 16);
    }

    public boolean hasBlockAtRelative(int x, int y, int z) {
        return this.getRelative(x, y, z) != null;
    }

    public boolean isRelativePositionInChunk(int x, int y, int z) {
        return x >= 0 && x < 16 && y >= 0 && y < 128 && z >= 0 && z < 16;
    }
}
