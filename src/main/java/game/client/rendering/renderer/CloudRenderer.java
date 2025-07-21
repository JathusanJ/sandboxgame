package game.client.rendering.renderer;

import engine.renderer.Shader;
import engine.renderer.Texture;
import game.client.SandboxGame;
import game.client.rendering.chunk.ChunkVertexBuilder;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class CloudRenderer {
    public int vaoId;
    public int vboId;
    public int eboId;
    public Texture texture;
    public int vboSize;

    public void setup() {
        this.texture = new Texture("textures/sky/clouds.png");

        float cloudHeight = 0F;
        float size = 200F;

        float[] vertices = {
                -size, cloudHeight, -size, 0F, 1F,
                size, cloudHeight, -size, 1F, 1F,
                size, cloudHeight, size, 1F, 0F,
                -size, cloudHeight, size, 0F, 0F
        };

        this.vboSize = vertices.length;

        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);

        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        int stride = 5 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
    }

    public void render(float scrollX, float scrollY) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        Shader shader = SandboxGame.getInstance().getGameRenderer().uiRenderer.texturedQuadShader;
        shader.use();
        shader.uploadMatrix4f("view", SandboxGame.getInstance().getGameRenderer().camera.getViewMatrixLookOnly());
        shader.uploadMatrix4f("projection", SandboxGame.getInstance().getGameRenderer().camera.getProjectionMatrix());
        shader.uploadMatrix4f("model", new Matrix4f().translate(0, 130F, 0));
        shader.uploadVector2f("scroll", new Vector2f(scrollX, scrollY));
        this.texture.bind();

        glBindVertexArray(this.vaoId);
        glDrawArrays(GL_TRIANGLES, 0, this.vboSize);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        // uploaded shader values persist until overwritten it seems
        shader.uploadVector2f("scroll", new Vector2f(0, 0));
    }
}
