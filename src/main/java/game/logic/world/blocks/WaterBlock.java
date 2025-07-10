package game.logic.world.blocks;

import game.client.rendering.chunk.ChunkVertexBuilder;
import game.client.world.ClientChunk;
import game.logic.world.World;
import org.joml.Vector2f;
import org.joml.Vector3i;

public class WaterBlock extends Block {
    @Override
    public Vector2f[] getTextures() {
        return new Vector2f[]{
                new Vector2f(9, 9), // TOP
                new Vector2f(9, 9), // BOTTOM
                new Vector2f(9, 9), // RIGHT
                new Vector2f(9, 9), // LEFT
                new Vector2f(9, 9), // FRONT
                new Vector2f(9, 9) // BACK
        };
    }

    @Override
    public boolean hasCollision() {
        return false;
    }

    @Override
    public boolean canLightPassThrough() {
        return true;
    }

    @Override
    public int getLightPassThroughPenalty() {
        return 2;
    }


    @Override
    public boolean shouldCreateTopFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this;
    }

    @Override
    public boolean shouldCreateBottomFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this && neighboringBlock.isEmpty();
    }

    @Override
    public boolean shouldCreateFrontFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this && neighboringBlock.isEmpty();
    }

    @Override
    public boolean shouldCreateBackFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this && neighboringBlock.isEmpty();
    }

    @Override
    public boolean shouldCreateRightFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this && neighboringBlock.isEmpty();
    }

    @Override
    public boolean shouldCreateLeftFace(World world, int x, int y, int z, Block neighboringBlock) {
        return neighboringBlock != this && neighboringBlock.isEmpty();
    }

    @Override
    public void buildBlockVertices(ChunkVertexBuilder vertexBuilder, ClientChunk chunk, int x, int y, int z) {
        float height = 0.9F;
        if(chunk.getBlockAtLocalizedPosition(x, y + 1, z) == Blocks.WATER) {
            height = 1F;
        }


        // Top side
        Vector3i worldPosition = new Vector3i(chunk.chunkPosition.x * 16 + x, y, chunk.chunkPosition.z * 16 + z);
        if(this.shouldCreateTopFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x, worldPosition.y + 1, worldPosition.z))){
            float skylight = chunk.world.getSkylightAt(worldPosition.x, worldPosition.y + 1, worldPosition.z) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x, worldPosition.y + 1, worldPosition.z) / 16F;

            vertexBuilder.vertex(x + 1, y + height, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z, 1, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z, 0, 1, ChunkVertexBuilder.Normal.TOP, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, this, skylight, light);

            // Also create another face facing towards the bottom to make the surface visible from within the water
            vertexBuilder.vertex(x, y + height, z, 0, 1, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z, 1, 1, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z + 1, 1, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z + 1, 0, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
        }

        // Bottom side
        if(this.shouldCreateBottomFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x, worldPosition.y - 1, worldPosition.z))) {
            float skylight = chunk.world.getSkylightAt(worldPosition.x, worldPosition.y - 1, worldPosition.z) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x, worldPosition.y - 1, worldPosition.z) / 16F;

            vertexBuilder.vertex(x, y, z, 0, height, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z, 1, height, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
            vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.BOTTOM, this, skylight, light);
        }

        // Right side
        if(this.shouldCreateRightFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x + 1, worldPosition.y, worldPosition.z))) {
            float skylight = chunk.world.getSkylightAt(worldPosition.x + 1, worldPosition.y, worldPosition.z) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x + 1, worldPosition.y, worldPosition.z) / 16F;

            vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z, 1, height, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z + 1, 0, height, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.RIGHT, this, skylight, light);
        }

        // Left side
        if(this.shouldCreateLeftFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x - 1, worldPosition.y, worldPosition.z))) {
            float skylight = chunk.world.getSkylightAt(worldPosition.x - 1, worldPosition.y, worldPosition.z) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x - 1, worldPosition.y, worldPosition.z) / 16F;

            vertexBuilder.vertex(x, y + height, z + 1, 1, height, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z, 0, height, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
            vertexBuilder.vertex(x, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.LEFT, this, skylight, light);
        }


        // Front side
        if(this.shouldCreateFrontFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x, worldPosition.y, worldPosition.z + 1))) {
            float skylight = chunk.world.getSkylightAt(worldPosition.x, worldPosition.y, worldPosition.z + 1) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x, worldPosition.y, worldPosition.z + 1) / 16F;

            vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x + 1, y + height, z + 1, 1, height, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z + 1, 0, height, ChunkVertexBuilder.Normal.FRONT, this, skylight, light);
        }

        // Back side
        if(this.shouldCreateBackFace(chunk.world, worldPosition.x, worldPosition.y, worldPosition.z, chunk.world.getBlockAt(worldPosition.x, worldPosition.y, worldPosition.z - 1))) {
            float skylight = chunk.world.getSkylightAt(worldPosition.x, worldPosition.y, worldPosition.z - 1) / 16F;
            float light = chunk.world.getLightAt(worldPosition.x, worldPosition.y, worldPosition.z - 1) / 16F;

            vertexBuilder.vertex(x + 1, y + height, z, 0, height, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x + 1, y, z, 0, 0, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x, y, z, 1, 0, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
            vertexBuilder.vertex(x, y + height, z, 1, height, ChunkVertexBuilder.Normal.BACK, this, skylight, light);
        }
    }

    @Override
    public boolean isLiquid() {
        return true;
    }
}
