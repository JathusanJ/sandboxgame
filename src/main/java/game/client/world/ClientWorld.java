package game.client.world;

import game.client.SandboxGame;
import game.logic.world.World;
import game.logic.world.chunk.Chunk;
import game.logic.world.creature.Creature;
import game.client.networking.GameClient;
import org.joml.Vector2i;

import java.util.ArrayList;

public class ClientWorld extends World {
    public int renderDistanceOverride = -1;

    public void deleteChunkMeshes() {
        for(Chunk chunk : this.loadedChunks.values()) {
            if(!chunk.isReady()) {
                continue;
            }
            if(((ClientChunk) chunk).chunkMesh != null) {
                SandboxGame.getInstance().doOnMainThread(() -> {
                    ((ClientChunk) chunk).chunkMesh.delete();
                });
            }
        }
    }

    @Override
    public void unloadChunk(Chunk chunk) {
        if(((ClientChunk) chunk).chunkMesh != null) {
            SandboxGame.getInstance().doOnMainThread(() -> {
                ((ClientChunk) chunk).chunkMesh.delete();
            });
        }
        super.unloadChunk(chunk);
    }

    @Override
    public Chunk createChunk(int x, int y) {
        return new ClientChunk(x, y, this);
    }
}
