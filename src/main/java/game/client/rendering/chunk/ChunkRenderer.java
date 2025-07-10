package game.client.rendering.chunk;

import game.client.rendering.renderer.GameRenderer;
import engine.renderer.Shader;
import engine.renderer.Texture;
import game.client.world.ClientChunk;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Collection;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class ChunkRenderer {
    public Shader shader;
    public Texture texture;

    public int eboId;
    public int currentEboLength = 0;

    public GameRenderer gameRenderer;

    public ArrayList<ChunkMesh> uploadQueue = new ArrayList<>();

    public int chunksRendered = 0;
    public int verticesRendered = 0;

    public ChunkMeshGenerationManager chunkMeshGenerationManager = new ChunkMeshGenerationManager();

    public ChunkRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }

    public void setup() {
        this.shader = new Shader("shaders/chunk.vertex.glsl", "shaders/chunk.fragment.glsl");
        this.texture = new Texture("textures/blocks.png");

        this.eboId = glGenBuffers();
    }

    public void createVertexAttributes() {
        int stride = 8 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, 1, GL_FLOAT, false, stride, 6 * Float.BYTES);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, 1, GL_FLOAT, false, stride, 7 * Float.BYTES);
        glEnableVertexAttribArray(4);
    }

    public void renderChunks(Collection<ClientChunk> chunks, float sunLight) {
        this.chunksRendered = 0;
        this.verticesRendered = 0;

        this.shader.use();
        this.texture.bind();

        Matrix4f view = this.gameRenderer.camera.getViewMatrix();
        Matrix4f projection = this.gameRenderer.camera.getProjectionMatrix();

        this.shader.uploadMatrix4f("view", view);
        this.shader.uploadMatrix4f("projection", projection);
        this.shader.uploadFloat("sunLight", sunLight);

        this.chunkMeshGenerationManager.tick();

        for(ClientChunk chunk : chunks.stream().sorted().toList()) {
            if(chunk.chunkMesh == null) {
                chunk.chunkMesh = new ChunkMesh(chunk);
                this.chunkMeshGenerationManager.queue.add(chunk);
                continue;
            } else if(chunk.needsRemesh()) {
                chunk.chunkMesh.generate();
                if(chunk.chunkMesh.state == ChunkMesh.State.FAILED) {
                    chunk.chunkMesh = null;
                } else {
                    chunk.chunkMesh.upload();
                }
                chunk.noLongerNeedsRemesh();
            }
            if(chunk.chunkMesh != null && chunk.chunkMesh.state == ChunkMesh.State.WAITING_FOR_UPLOAD) {
                chunk.chunkMesh.upload();
            }

            if(chunk.chunkMesh == null || chunk.chunkMesh.state != ChunkMesh.State.COMPLETED) continue;

            if(chunk.chunkMesh.length / 4 > this.currentEboLength) {
                //System.out.println("Increasing size of elements buffer from " + this.currentEboLength + " to " + chunk.chunkMesh.length / 4);
                int[] indices = new int[chunk.chunkMesh.length / 4 * 6];

                for(int i = 0; i < chunk.chunkMesh.length / 4; i++) {
                    int offset = i * 6;
                    int vertexOffset = i * 4;

                    indices[offset] = vertexOffset;
                    indices[offset + 1] = vertexOffset + 1;
                    indices[offset + 2] = vertexOffset + 2;

                    indices[offset + 3] = vertexOffset + 2;
                    indices[offset + 4] = vertexOffset + 3;
                    indices[offset + 5] = vertexOffset;
                }

                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.eboId);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) indices.length * Integer.BYTES, GL_STATIC_DRAW);
                glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indices);

                this.currentEboLength = indices.length / 6;
            }

            glBindVertexArray(chunk.chunkMesh.vaoId);
            glDrawElements(GL_TRIANGLES, chunk.chunkMesh.length / 4, GL_UNSIGNED_INT, 0);

            this.chunksRendered++;
            this.verticesRendered = this.verticesRendered + chunk.chunkMesh.length / 8;
        }
    }
}
