package game.logic.world.chunk;

import game.logic.world.ServerWorld;
import org.joml.Vector2i;
import org.joml.Vector3i;

public class ServerChunk extends Chunk {
    public ServerChunk(Vector2i chunkPosition, ServerWorld world) {
        this.chunkPosition = new Vector3i(chunkPosition.x, 0, chunkPosition.y);
        this.world = world;
    }
}
