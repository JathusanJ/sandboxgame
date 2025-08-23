package game.shared.world.blocks.block_entity;

import game.shared.util.json.WrappedJsonObject;
import game.shared.world.World;
import game.shared.world.chunk.Chunk;
import game.shared.world.creature.ItemCreature;
import game.shared.world.items.ItemStack;
import org.joml.Vector2i;
import org.joml.Vector3i;

public abstract class BlockEntity {
    public Vector3i position;
    public World world;

    public abstract void tick();
    public abstract void save(WrappedJsonObject json);
    public abstract void load(WrappedJsonObject json);
    public void onDestroy() {}

    public void needsSaving() {
        Vector2i chunkPosition = this.world.getChunkPositionOfPosition(this.position.x, this.position.y, this.position.z);
        Chunk chunk = this.world.getChunk(chunkPosition.x, chunkPosition.y);
        if(chunk == null) {
            return;
        }

        chunk.setModified();
    }
}
