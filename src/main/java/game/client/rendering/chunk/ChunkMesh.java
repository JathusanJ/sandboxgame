package game.client.rendering.chunk;

import game.client.SandboxGame;
import game.logic.world.blocks.Block;
import game.client.world.ClientChunk;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ChunkMesh {
    public ClientChunk chunk;
    public int vaoId;
    public int vboId;
    public int length = 0;
    public State state = State.UNINITIALIZED;
    public Task task;

    public ChunkMesh(ClientChunk chunk) {
        this.chunk = chunk;
        this.vboId = glGenBuffers();
        this.vaoId = glGenVertexArrays();
    }

    public void upload() {
        try {
            glBindVertexArray(this.vaoId);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, SandboxGame.getInstance().getGameRenderer().chunkRenderer.eboId);
            glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
            glBufferData(GL_ARRAY_BUFFER, (long) this.task.vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.task.vertices);
            SandboxGame.getInstance().getGameRenderer().chunkRenderer.createVertexAttributes();
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
        glDeleteVertexArrays(this.vaoId);
    }

    private float[] generateVertices() {
        ChunkVertexBuilder vertexBuilder = new ChunkVertexBuilder();

        for(int y = 0; y < 128; y++) {
            for(int x = 0; x < 16; x++) {
                for(int z = 0; z < 16; z++) {
                    Block block = chunk.getBlockAtLocalizedPosition(x,y,z);
                    if(!block.isEmpty()) {
                        // Build a cube
                       block.buildBlockVertices(vertexBuilder, this.chunk, x, y, z);
                    }
                }
            }
        }

        float[] vertices = vertexBuilder.compile(this.chunk.chunkPosition.x * 16, 0, this.chunk.chunkPosition.y * 16);
        this.length = vertices.length;
        return vertices;
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
        public ChunkMesh chunkMesh;

        public Task(ChunkMesh chunkMesh) {
            this.chunkMesh = chunkMesh;
        }

        public void run() {
            this.vertices = this.chunkMesh.generateVertices();
            this.chunkMesh.state = State.WAITING_TO_BE_ENQUEUED;
        }
    }
}
