package game.client.world;

import game.client.SandboxGame;
import game.client.rendering.chunk.ChunkMesh;
import game.shared.world.World;
import game.shared.world.chunk.Chunk;
import game.shared.world.blocks.Block;
import game.client.multiplayer.GameClient;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class ClientChunk extends Chunk implements Comparable<ClientChunk> {
    public ChunkMesh chunkMesh;
    private boolean needsRemesh = false;

    public ClientChunk(int x, int y, World world) {
        this.chunkPosition = new Vector2i(x,y);
        this.world = world;
    }

    @Override
    public boolean setBlockAtLocalizedPosition(int x, int y, int z, Block block) {
        if(x > 15 || x < 0 || y > 128 || y < 0 || z > 15 || z < 0) {
            return false;
        }

        if(!super.setBlockAtLocalizedPosition(x,y,z, block)) {
            return false;
        }

        this.setNeedsRemesh();
        if(x == 0) {
            // Remesh the chunk left of the current chunk
            ClientChunk chunk = (ClientChunk) this.world.getChunk(this.chunkPosition.x - 1, this.chunkPosition.y);
            if(chunk != null) {
                chunk.setNeedsRemesh();
            }
        } else if(x == 15) {
            // Remesh the chunk right of the current chunk
            ClientChunk chunk = (ClientChunk) this.world.getChunk(this.chunkPosition.x + 1, this.chunkPosition.y);
            if(chunk != null) {
                chunk.setNeedsRemesh();
            }
        }
        if(z == 0) {
            // Remesh the chunk behind of the current chunk
            ClientChunk chunk = (ClientChunk) this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y - 1);
            if(chunk != null) {
                chunk.setNeedsRemesh();
            }
        } else if(z == 15) {
            // Remesh the chunk in front of the current chunk
            ClientChunk chunk = (ClientChunk) this.world.getChunk(this.chunkPosition.x, this.chunkPosition.y + 1);
            if(chunk != null) {
                chunk.setNeedsRemesh();
            }
        }

        return true;
    }

    public void setNeedsRemesh() {
        this.needsRemesh = true;
    }

    public boolean needsRemesh() {
        return this.needsRemesh;
    }

    public void noLongerNeedsRemesh() {
        this.needsRemesh = false;
    }

    @Override
    public void save() {
        if(GameClient.isConnectedToServer) return;
        super.save();
    }

    @Override
    public int compareTo(@NotNull ClientChunk o) {
        Vector3f playerPosition = SandboxGame.getInstance().getGameRenderer().player.position;
        float thisDistance = new Vector2f(this.chunkPosition.x * 16 + 8 - playerPosition.x, this.chunkPosition.y * 16 + 8 - playerPosition.z).length();
        float otherDistance = new Vector2f(o.chunkPosition.x * 16 + 8 - playerPosition.x, o.chunkPosition.y * 16 + 8 - playerPosition.z).length();

        return Float.compare(otherDistance, thisDistance);
    }
}
