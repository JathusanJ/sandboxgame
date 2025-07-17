package game.logic.world.chunk;

import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;
import org.joml.Vector3i;

import java.util.HashMap;

// Not the best way, but I want things to work at the moment
public class ChunkProxy {
    public Chunk chunk;

    public ChunkProxy(Chunk chunk) {
        this.chunk = chunk;
    }

    public void setRelative(int x, int y, int z, Block block) {
        if(this.isRelativePositionInChunk(x,y,z)) {
            this.chunk.setBlockAtLocalizedPositionDirect(x, y, z, block);
        } else {
            this.chunk.world.setBlockAtDirect(this.chunk.chunkPosition.x * 16 + x, y, this.chunk.chunkPosition.z * 16 + z, block);
        }
    }

    public void setRelativeIfAbsent(int x, int y, int z, Block block) {
        if(this.getRelative(x,y,z) == null) {
            this.setRelative(x,y,z, block);
        }
    }

    public Block getRelative(int x, int y, int z) {
        if(this.isRelativePositionInChunk(x,y,z)) {
            return this.chunk.getBlockAtLocalizedPositionDirect(x, y, z);
        }

        // TODO: Implement a fix rather than this
        try {
            return this.chunk.world.getBlockAtDirect(this.chunk.chunkPosition.x * 16 + x, y, this.chunk.chunkPosition.z * 16 + z);
        } catch(Exception ignored) {

        }
        return null;
    }

    public boolean hasBlockAtRelative(int x, int y, int z) {
        return this.getRelative(x, y, z) != null;
    }

    public boolean isRelativePositionInChunk(int x, int y, int z) {
        return x >= 0 && x < 16 && y >= 0 && y < 128 && z >= 0 && z < 16;
    }
}
