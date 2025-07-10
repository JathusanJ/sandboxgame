package game.client.rendering.renderer;

import engine.renderer.Shader;
import game.client.SandboxGame;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class BlockSelectorRenderer {
    public int vaoId;
    public int vboId;
    public int vboLength;
    public Shader shader;

    public void setup() {
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);

        float[] data = new float[]{
                // Bottom
                0F, 0F, 0F,
                1F, 0F, 0F,

                0F, 0F, 0F,
                0F, 0F, 1F,

                1F, 0F, 0F,
                1F, 0F, 1F,

                1F, 0F, 1F,
                0F, 0F, 1F,

                // Top
                0F, 1F, 0F,
                1F, 1F, 0F,

                0F, 1F, 0F,
                0F, 1F, 1F,

                1F, 1F, 0F,
                1F, 1F, 1F,

                1F, 1F, 1F,
                0F, 1F, 1F,

                // Sides
                0F, 0F, 0F,
                0F, 1F, 0F,

                1F, 0F, 0F,
                1F, 1F, 0F,

                1F, 0F, 1F,
                1F, 1F, 1F,

                0F, 0F, 1F,
                0F, 1F, 1F
        };

        for (int i = 0; i < data.length; i++) {
            if(data[i] == 1F) {
                data[i] = 1.005F;
            } else {
                data[i] = -0.005F;
            }
        }

        this.vboLength = data.length;

        glBufferData(GL_ARRAY_BUFFER, (long) data.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        this.shader = new Shader("shaders/block_selector.vertex.glsl", "shaders/block_selector.fragment.glsl");
    }

    public void render(Vector3i position) {
        Matrix4f model = new Matrix4f().translate(position.x, position.y, position.z);

        glBindVertexArray(this.vaoId);

        this.shader.use();
        this.shader.uploadMatrix4f("model", model);
        this.shader.uploadMatrix4f("view", SandboxGame.getInstance().getGameRenderer().camera.getViewMatrix());
        this.shader.uploadMatrix4f("projection", SandboxGame.getInstance().getGameRenderer().camera.getProjectionMatrix());

        glDrawArrays(GL_LINES, 0, this.vboLength);
    }
}
