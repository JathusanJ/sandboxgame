package game.server.world;

import game.shared.world.chunk.Chunk;
import org.joml.Vector2i;

public class ServerChunk extends Chunk {
    public ServerChunk(int x, int y, ServerWorld world) {
        this.chunkPosition = new Vector2i(x,y);
        this.world = world;
    }
}
