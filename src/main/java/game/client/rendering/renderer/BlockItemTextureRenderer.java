package game.client.rendering.renderer;

import engine.renderer.Camera;
import engine.renderer.Texture;
import game.client.SandboxGame;
import game.client.rendering.chunk.ChunkVertexBuilder;
import game.shared.world.blocks.Block;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.*;

public class BlockItemTextureRenderer {
    public GameRenderer gameRenderer;
    public int fbo;
    public int rbo;
    public int vao;
    public int vbo;
    public Camera camera = new Camera();

    public BlockItemTextureRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;

        this.camera.orthographicProjection(1,1);
        this.camera.position.add(7F, 7F, 12.75F);
        this.camera.pitch = -35F;
        this.camera.yaw = -135F;
    }

    public void setup() {
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);

        glViewport(0, 0, 800, 600);

        this.rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, this.rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, 800, 600);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, this.rbo);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            SandboxGame.getInstance().logger.error("Failed to setup block item rendering: {}", glCheckFramebufferStatus(GL_FRAMEBUFFER));
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());

        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);

        this.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferData(GL_ARRAY_BUFFER, (long) 100 * Float.BYTES, GL_DYNAMIC_DRAW);

        this.gameRenderer.chunkRenderer.createVertexAttributes();
    }

    public Texture render(Block block) {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        int texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 800, 600, 0, GL_RGBA, GL_UNSIGNED_BYTE, MemoryUtil.NULL);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        ChunkVertexBuilder vertexBuilder = new ChunkVertexBuilder();

        int x = 0;
        int y = 0;
        int z = 0;

        // Top side
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, block);

        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 0, 0, ChunkVertexBuilder.Normal.TOP, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 0, ChunkVertexBuilder.Normal.TOP, block);

        // Right side
        vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y + 1, z, 1, 1, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.RIGHT, block);

        vertexBuilder.vertex(x + 1, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.RIGHT, block);
        vertexBuilder.vertex(x + 1, y, z, 1, 0, ChunkVertexBuilder.Normal.RIGHT, block);

        // Left side
        vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y + 1, z, 0, 1, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, block);

        vertexBuilder.vertex(x, y, z, 0, 0, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.LEFT, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.LEFT, block);

        // Front side
        vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x + 1, y, z + 1, 1, 0, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.FRONT, block);

        vertexBuilder.vertex(x + 1, y + 1, z + 1, 1, 1, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x, y + 1, z + 1, 0, 1, ChunkVertexBuilder.Normal.FRONT, block);
        vertexBuilder.vertex(x, y, z + 1, 0, 0, ChunkVertexBuilder.Normal.FRONT, block);

        float[] vertices = vertexBuilder.compile(0, 0, 5);

        this.gameRenderer.chunkRenderer.shader.use();
        this.gameRenderer.chunkRenderer.shader.uploadMatrix4f("view", this.camera.getViewMatrix());
        this.gameRenderer.chunkRenderer.shader.uploadMatrix4f("projection", this.camera.getProjectionMatrix());
        this.gameRenderer.chunkRenderer.shader.uploadFloat("sunLight", 1F);

        this.gameRenderer.chunkRenderer.texture.bind();

        glViewport(0, 0, 800, 600);

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glEnable(GL_CULL_FACE);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 8);
        glDisable(GL_CULL_FACE);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0, 0, SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());

        return new Texture(texture);
    }
}
