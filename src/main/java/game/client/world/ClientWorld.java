package game.client.world;

import game.client.SandboxGame;
import game.client.rendering.chunk.ChunkMesh;
import game.shared.world.World;
import game.shared.world.chunk.Chunk;

import java.util.ArrayList;

public class ClientWorld extends World {
    public void deleteChunkMeshes() {
        ArrayList<ChunkMesh> chunkMeshes = new ArrayList<>();
        for(Chunk chunk : this.loadedChunks.values()) {
            if(!chunk.isReady()) {
                continue;
            }
            if(((ClientChunk) chunk).chunkMesh != null) {
                chunkMeshes.add(((ClientChunk) chunk).chunkMesh);
            }
        }
        SandboxGame.getInstance().doOnMainThread(() -> {
            chunkMeshes.forEach(ChunkMesh::delete);
        });
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
