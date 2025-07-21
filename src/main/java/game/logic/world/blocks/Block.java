package game.logic.world.blocks;

import game.client.rendering.chunk.ChunkVertexBuilder;
import game.client.world.ClientChunk;
import game.client.world.ClientWorld;
import game.logic.world.World;
import game.logic.world.creature.Player;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class Block {
    private String blockId;
    public Item asItem;

    public boolean isEmpty() {
        return false;
    }

    public boolean hasCollision() {
        return true;
    }

    public boolean canLightPassThrough() {
        return false;
    }

    public int getLightPassThroughPenalty() {
        return 1;
    }

    // TODO: Redo this mess and put it into a single method (I genuinely hate this so much)
    public boolean shouldCreateTopFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public boolean shouldCreateBottomFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public boolean shouldCreateFrontFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public boolean shouldCreateBackFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public boolean shouldCreateRightFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public boolean shouldCreateLeftFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock.isEmpty() || neighboringBlock.isLiquid() || neighboringBlock instanceof LeafBlock || neighboringBlock instanceof CrossBlock;
    }

    public void buildBlockVertices(ChunkVertexBuilder vertexBuilder, ClientChunk chunk, int x, int y, int z) {
        // Top side
        Vector3i worldPosition = new Vector3i(chunk.chunkPosition.x * 16 + x, y, chunk.chunkPosition.y * 16 + z);
        if(this.shouldCreateTopFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x, worldPosition.y + 1, worldPosition.z))){
            float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y + 1, worldPosition.z) / 16F;
            float light = chunk.world.getLight(worldPosition.x, worldPosition.y + 1, worldPosition.z) / 16F;

            vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x, y + 1, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
        }

        // Bottom side
        if(this.shouldCreateBottomFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x, worldPosition.y - 1, worldPosition.z))) {
            float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y - 1, worldPosition.z) / 16F;
            float light = chunk.world.getLight(worldPosition.x, worldPosition.y - 1, worldPosition.z) / 16F;

            vertexBuilder.vertex(x, y, z, 0, 1, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z, 1, 1, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
        }

        // Right side
        if(this.shouldCreateRightFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x + 1, worldPosition.y, worldPosition.z))) {
            float skylight = chunk.world.getSkylight(worldPosition.x + 1, worldPosition.y, worldPosition.z) / 16F;
            float light = chunk.world.getLight(worldPosition.x + 1, worldPosition.y, worldPosition.z) / 16F;

            vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
        }

        // Left side
        if(this.shouldCreateLeftFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x - 1, worldPosition.y, worldPosition.z))) {
            float skylight = chunk.world.getSkylight(worldPosition.x - 1, worldPosition.y, worldPosition.z) / 16F;
            float light = chunk.world.getLight(worldPosition.x - 1, worldPosition.y, worldPosition.z) / 16F;

            vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
        }


        // Front side
        if(this.shouldCreateFrontFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x, worldPosition.y, worldPosition.z + 1))) {
            float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y, worldPosition.z + 1) / 16F;
            float light = chunk.world.getLight(worldPosition.x, worldPosition.y, worldPosition.z + 1) / 16F;

            vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
        }

        // Back side
        if(this.shouldCreateBackFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlock(worldPosition.x, worldPosition.y, worldPosition.z - 1))) {
            float skylight = chunk.world.getSkylight(worldPosition.x, worldPosition.y, worldPosition.z - 1) / 16F;
            float light = chunk.world.getLight(worldPosition.x, worldPosition.y, worldPosition.z - 1) / 16F;

            vertexBuilder.vertex(x + 1, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z, 0, 0, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x, y, z, 1, 0, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
        }
    }

    public Vector2f[] getTextures() {
        return new Vector2f[]{
            new Vector2f(), // TOP
            new Vector2f(), // BOTTOM
            new Vector2f(), // RIGHT
            new Vector2f(), // LEFT
            new Vector2f(), // FRONT
            new Vector2f() // BACK
        };
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public boolean onRightClick(ClientWorld world, Vector3i blockPosition) {
        return true;
    }

    public int getBlockBreakingTicks(Player player, ItemStack heldItemStack) {
        return 20;
    }

    public ItemStack getAsDroppedItem(Player player, ItemStack tool) {
        return new ItemStack(this.getAsItem());
    }

    public Item getAsItem() {
        return this.asItem;
    }

    public boolean isLiquid() {
        return false;
    }
}
