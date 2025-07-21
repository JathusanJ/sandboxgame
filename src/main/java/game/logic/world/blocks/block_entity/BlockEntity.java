package game.logic.world.blocks.block_entity;

import game.logic.util.json.WrappedJsonObject;
import game.logic.world.World;
import game.logic.world.chunk.Chunk;
import org.joml.Vector2i;
import org.joml.Vector3i;

public abstract class BlockEntity {
    public Vector3i position;
    public World world;

    public abstract void tick();
    public abstract void save(WrappedJsonObject json);
    public abstract void load(WrappedJsonObject json);

    public void needsSaving() {
        Vector2i chunkPosition = this.world.getChunkPositionOfPosition(this.position.x, this.position.y, this.position.z);
        Chunk chunk = this.world.getChunk(chunkPosition.x, chunkPosition.y);
        if(chunk == null) {
            throw new IllegalStateException("Block entity tried to mark parent chunk as modified, but the parent chunk doesn't exist or the block entity is in the wrong chunk");
        }

        chunk.setModified();
    }
}
