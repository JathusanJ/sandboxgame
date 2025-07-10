package game.logic.world.blocks.block_entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.World;
import game.logic.world.chunk.Chunk;
import org.joml.Vector3i;

public abstract class BlockEntity {
    public Vector3i position;
    public World world;

    public abstract void tick();
    public abstract void save(WrappedJsonObject json);
    public abstract void load(WrappedJsonObject json);

    public void needsSaving() {
        Chunk chunk = this.world.getChunkAtBlockPosition(this.position);
        if(chunk == null) {
            throw new IllegalStateException("Block entity tried to mark parent chunk as modified, but the parent chunk doesn't exist or the block entity is in the wrong chunk");
        }

        chunk.setModified();
    }
}
