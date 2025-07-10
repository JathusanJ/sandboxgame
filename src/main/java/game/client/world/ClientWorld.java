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
            if(chunk.state != Chunk.ChunkState.READY) {
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
        if(((ClientChunk) chunk).chunkMesh != null && chunk.state == Chunk.ChunkState.READY) {
            SandboxGame.getInstance().doOnMainThread(() -> {
                ((ClientChunk) chunk).chunkMesh.delete();
            });
        }
        super.unloadChunk(chunk);
        if(GameClient.isConnectedToServer) {
            for (int i = 0; i < this.creatures.size(); i++) {
                Creature creature = this.creatures.get(i);

                if(creature.getChunkPosition().equals(new Vector2i(chunk.chunkPosition.x, chunk.chunkPosition.z))){
                    this.creatures.remove(creature);
                }
            }
        }
    }

    @Override
    public Chunk createChunk(Vector2i chunkPosition) {
        return new ClientChunk(chunkPosition, this);
    }

    @Override
    public int getRenderDistance() {
        return renderDistanceOverride < 0 ? SandboxGame.getInstance().settings.renderDistance : renderDistanceOverride;
    }

    @Override
    public void generateChunksAround(int x, int z) {
        if(this instanceof SingleplayerWorld) {
            super.generateChunksAround(x, z);
        } else {
            int radius = this.getRenderDistance() + 1;
            int loadedRadius = radius + 4;
            ArrayList<Vector2i> chunksToUnload = new ArrayList<>(this.loadedChunks.keySet());

            for (int chunkX = x - loadedRadius; chunkX <= x + loadedRadius; chunkX++) {
                for (int chunkZ = z - loadedRadius; chunkZ <= z + loadedRadius ; chunkZ++) {
                    if(Math.abs(chunkX - x) <= radius && Math.abs(chunkZ - z) <= radius && this.getChunkAt(chunkX, chunkZ) == null) {
                        Vector2i chunkPos = new Vector2i(chunkX, chunkZ);
                        if(!GameClient.chunksToRequest.contains(chunkPos)) {
                            GameClient.chunksToRequest.add(chunkPos);
                        }
                    } else {
                        chunksToUnload.remove(new Vector2i(chunkX, chunkZ));
                    }
                }
            }

            for(Vector2i chunkToUnload : chunksToUnload) {
                Chunk chunk = this.loadedChunks.get(chunkToUnload);
                this.unloadChunk(chunk);
            }
        }
    }
}
