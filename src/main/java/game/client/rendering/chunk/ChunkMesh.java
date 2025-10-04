package game.client.rendering.chunk;

import game.client.SandboxGame;
import game.shared.world.blocks.Block;
import game.client.world.ClientChunk;
import game.shared.world.blocks.Blocks;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL30.*;

public class ChunkMesh {
    public ClientChunk chunk;
    public int vaoId;
    public int vboId;
    public int waterVaoId;
    public int waterVboId;
    public int length = 0;
    public int waterLength = 0;
    public State state = State.UNINITIALIZED;
    public Task task;

    public ChunkMesh(ClientChunk chunk) {
        this.chunk = chunk;
        this.vboId = glGenBuffers();
        this.vaoId = glGenVertexArrays();
        this.waterVaoId = glGenVertexArrays();
        this.waterVboId = glGenBuffers();
    }

    public void upload() {
        try {
            glBindVertexArray(this.vaoId);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, SandboxGame.getInstance().getGameRenderer().chunkRenderer.eboId);
            glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
            glBufferData(GL_ARRAY_BUFFER, (long) this.task.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.task.vertices);
            SandboxGame.getInstance().getGameRenderer().chunkRenderer.createVertexAttributes();

            if(this.waterLength > 0) {
                glBindVertexArray(this.waterVaoId);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, SandboxGame.getInstance().getGameRenderer().chunkRenderer.eboId);
                glBindBuffer(GL_ARRAY_BUFFER, this.waterVboId);
                glBufferData(GL_ARRAY_BUFFER, (long) this.task.waterVertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
                glBufferSubData(GL_ARRAY_BUFFER, 0, this.task.waterVertices);
                SandboxGame.getInstance().getGameRenderer().chunkRenderer.createVertexAttributes();
            }
            this.state = State.COMPLETED;
        } catch(Exception e) {
            SandboxGame.getInstance().logger.error("Failed upload of chunk mesh", e);
            this.chunk.chunkMesh = null;
        }

        this.task = null;
    }

    public void generate() {
        this.state = State.BUILDING;
        this.task = new Task(this);
        try {
            this.task.run();
        } catch(Exception e) {
            e.printStackTrace();
            this.state = State.FAILED;
        }
    }

    public void delete() {
        glDeleteBuffers(this.vboId);
        glDeleteBuffers(this.waterVboId);
        glDeleteVertexArrays(this.vaoId);
        glDeleteVertexArrays(this.waterVaoId);
    }

    private void generateVertices(Task task) {
        ChunkVertexBuilder vertexBuilder = new ChunkVertexBuilder();
        ChunkVertexBuilder waterVertexBuilder = new ChunkVertexBuilder();

        for(int y = 0; y < 128; y++) {
            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    Block block = chunk.getBlockAtLocalizedPosition(x,y,z);
                    if(!block.isEmpty()) {
                        if(block == Blocks.WATER) {
                            block.buildBlockVertices(waterVertexBuilder, this.chunk, x, y, z);
                        } else {
                            block.buildBlockVertices(vertexBuilder, this.chunk, x, y, z);
                        }
                    }
                }
            }
        }

        task.vertices = vertexBuilder.compile(this.chunk.chunkPosition.x * 16, 0, this.chunk.chunkPosition.y * 16);
        this.length = task.vertices.length;

        task.waterVertices = waterVertexBuilder.compile(this.chunk.chunkPosition.x * 16, 0, this.chunk.chunkPosition.y * 16);
        this.waterLength = task.waterVertices.length;
    }

    public enum State {
        COMPLETED,
        WAITING_TO_BE_ENQUEUED,
        WAITING_FOR_UPLOAD,
        BUILDING,
        UNINITIALIZED,
        FAILED
    }

    public static class Task {
        public float[] vertices;
        public float[] waterVertices;
        public ChunkMesh chunkMesh;

        public Task(ChunkMesh chunkMesh) {
            this.chunkMesh = chunkMesh;
        }

        public void run() {
            this.chunkMesh.generateVertices(this);
            this.chunkMesh.state = State.WAITING_TO_BE_ENQUEUED;
        }
    }
}
