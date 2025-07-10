package game.client.rendering.renderer;

import game.client.SandboxGame;
import engine.renderer.Shader;
import engine.renderer.Texture;
import game.logic.util.Spline;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class SkyRenderer {

    public int vaoId;
    public int vboId;
    public Shader skyShader;
    public Texture sunTexture;
    public float[] data;
    public Spline skyColorSpline;

    public void setup() {
        ArrayList<Float> vertices = new ArrayList<>();

        float sunSize = 100F;

        float sunX = -sunSize / 2F;
        float sunY = -sunSize / 2F;
        float sunZ = 500F;

        this.data = new float[]{
                sunX,           sunY,           -sunZ, 0, 0,
                sunX + sunSize, sunY,           -sunZ, 0.5F, 0,
                sunX + sunSize, sunY + sunSize, -sunZ, 0.5F, 1,
                sunX + sunSize, sunY + sunSize, -sunZ, 0.5F, 1,
                sunX          , sunY + sunSize, -sunZ, 0, 1,
                sunX          , sunY,           -sunZ, 0, 0,

                sunX,           sunY,           sunZ, 0.5F, 0,
                sunX + sunSize, sunY,           sunZ, 1, 0,
                sunX + sunSize, sunY + sunSize, sunZ, 1, 1,
                sunX + sunSize, sunY + sunSize, sunZ, 1, 1,
                sunX          , sunY + sunSize, sunZ, 0.5F, 1,
                sunX          , sunY,           sunZ, 0.5F, 0

        };

        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);

        glBufferData(GL_ARRAY_BUFFER, (long) this.data.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, this.data);

        int stride = 5 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        this.sunTexture = new Texture("textures/sky/sun.png");
        this.skyShader = new Shader("shaders/sky.vertex.glsl", "shaders/sky.fragment.glsl");

        this.skyColorSpline = new Spline(List.of(
                new Vector2f(0, 0.25F),
                new Vector2f(1000, 0.75F),
                new Vector2f(2000, 1F),
                new Vector2f(13000, 1F),
                new Vector2f(14000, 0.75F),
                new Vector2f(15500, 0.5F),
                new Vector2f(17500, 0),
                new Vector2f(27000, 0F),
                new Vector2f(28800, 0.25F)
        ));
    }

    public void delete() {
        this.sunTexture.delete();
        glDeleteVertexArrays(this.vaoId);
        glDeleteBuffers(this.vboId);
        this.skyShader.delete();
    }

    public void render() {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        float rotation = (float) ((Math.clamp(SandboxGame.getInstance().getGameRenderer().world.getDayTime() / (13F * 60 * 20), 0, 1) + Math.clamp((SandboxGame.getInstance().getGameRenderer().world.getDayTime() - (13F * 60 * 20)) / (11F * 60 * 20), 0, 1)) * Math.PI);
        float multiplier = this.skyColorSpline.calculateLinear(SandboxGame.getInstance().getGameRenderer().world.getDayTime());

        SandboxGame.getInstance().getGameRenderer().uiRenderer.renderColoredQuad(new Vector2f(0, 0), new Vector2f(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight()), new Vector4f((85F / 255F) * multiplier, (136F / 255F) * multiplier, 1F * multiplier, 1.0F));

        glBindVertexArray(this.vaoId);

        this.sunTexture.bind();
        this.skyShader.use();

        this.skyShader.uploadMatrix4f("view", SandboxGame.getInstance().getGameRenderer().camera.getViewMatrixLookOnly());
        this.skyShader.uploadMatrix4f("projection", SandboxGame.getInstance().getGameRenderer().camera.getProjectionMatrix());

        Matrix4f model = new Matrix4f().rotate(rotation, 1F, 0F, 0F);
        this.skyShader.uploadMatrix4f("model", model);

        glDrawArrays(GL_TRIANGLES, 0, this.data.length);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }
}
