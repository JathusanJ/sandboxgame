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
        this.chunk.setBlockAtLocalizedPositionDirect(x,y,z, block);
    }

    public void setRelativeIfAbsent(int x, int y, int z, Block block) {
        if(this.chunk.getBlockAtLocalizedPositionDirect(x,y,z) == null) {
            this.chunk.setBlockAtLocalizedPositionDirect(x,y,z, block);
        }
    }

    public Block getRelative(int x, int y, int z) {
        return this.chunk.getBlockAtLocalizedPositionDirect(x,y,z);
    }

    public boolean hasBlockAtRelative(int x, int y, int z) {
        return this.chunk.getBlockAtLocalizedPositionDirect(x,y,z) != null;
    }
}
