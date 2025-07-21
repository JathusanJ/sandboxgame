package game.logic.world.blocks;

import game.client.rendering.chunk.ChunkVertexBuilder;
import game.client.world.ClientChunk;
import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import org.joml.Vector3i;

public class CrossBlock extends Block {
    @Override
    public boolean hasCollision() {
        return false;
    }

    @Override
    public void buildBlockVertices(ChunkVertexBuilder vertexBuilder, ClientChunk chunk, int x, int y, int z) {
        Vector3i worldPosition = new Vector3i(chunk.chunkPosition.x * 16 + x, y, chunk.chunkPosition.y * 16 + z);

        float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y, worldPosition.z) / 16F;
        float light = chunk.world.getLight(worldPosition.x, worldPosition.y, worldPosition.z) / 16F;

        vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(x + 1, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y, z, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(x  , y, z, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(x + 1, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x  , y, z, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
    }

    @Override
    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        return 0;
    }
}
