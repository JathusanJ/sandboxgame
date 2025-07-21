package game.logic.world.blocks;

import game.client.rendering.chunk.ChunkVertexBuilder;
import game.client.world.ClientChunk;
import game.logic.world.creature.Player;
import game.logic.world.items.ItemStack;
import org.joml.Vector2f;
import org.joml.Vector3i;

import java.util.Random;

public class ShortGrassBlock extends CrossBlock {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(7, 2),
                new Vector2f(7, 2),
                new Vector2f(7, 2),
                new Vector2f(7, 2),
                new Vector2f(7, 2),
                new Vector2f(7, 2)
        };
    }

    @Override
    public ItemStack getAsDroppedItem(Player player, ItemStack tool) {
        return null;
    }

    @Override
    public void buildBlockVertices(ChunkVertexBuilder vertexBuilder, ClientChunk chunk, int x, int y, int z) {
        Vector3i worldPosition = new Vector3i(chunk.chunkPosition.x * 16 + x, y, chunk.chunkPosition.y * 16 + z);

        float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y, worldPosition.z) / 16F;
        float light = chunk.world.getLight(worldPosition.x, worldPosition.y, worldPosition.z) / 16F;

        Random offsetRandom = new Random((long) chunk.chunkPosition.x << 10 + chunk.chunkPosition.y * 20 + x * 50 + y * 16 + z * 4);
        float offset = offsetRandom.nextFloat() * 0.3F - 0.15F;
        float finalX = x + offset;
        float finalZ = z + offset;
        float finalY = y + offsetRandom.nextFloat() * 0.1F - 0.05F;

        vertexBuilder.vertex(finalX, finalY, finalZ + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY, finalZ, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY + 1, finalZ, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX, finalY + 1, finalZ + 1, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(finalX + 1, finalY + 1, finalZ, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY, finalZ, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX, finalY, finalZ + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX, finalY + 1, finalZ + 1, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(finalX  , finalY, finalZ, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY, finalZ + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY + 1, finalZ + 1, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX, finalY + 1, finalZ, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

        vertexBuilder.vertex(finalX + 1, finalY, finalZ + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX  , finalY, finalZ, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX, finalY + 1, finalZ, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        vertexBuilder.vertex(finalX + 1, finalY + 1, finalZ + 1, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
    }

    @Override
    public boolean isReplaceable() {
        return true;
    }
}
