package game.client.rendering.renderer;

import game.client.SandboxGame;
import engine.renderer.Texture;
import game.client.rendering.chunk.SimpleVertexBuilder;
import game.shared.world.creature.Player;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class BlockBreakingProgressRenderer {
    public int vaoId;
    public int vboId;
    public int vboLength;
    public Texture[] textures = new Texture[5];

    public void setup() {
        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);

        float[] data = this.generateCubeVertices();

        Matrix4f model = new Matrix4f();
        model.translate(-0.5F, -0.5F, -0.5F);
        model.scale(1.01F, 1.01F, 1.01F);

        for (int i = 0; i < data.length / 5; i++) {
            int offset = i * 5;
            Vector4f position =  new Vector4f(data[offset], data[offset + 1], data[offset + 2], 0F).mul(model);
            data[offset] = position.x;
            data[offset + 1] = position.y;
            data[offset + 2] = position.z;
        }

        glBufferData(GL_ARRAY_BUFFER, (long) data.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, data);

        int stride = 5 * Float.BYTES;

        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        this.vboLength = data.length;

        textures[0] = new Texture("textures/block_breaking/0.png");
        textures[1] = new Texture("textures/block_breaking/1.png");
        textures[2] = new Texture("textures/block_breaking/2.png");
        textures[3] = new Texture("textures/block_breaking/3.png");
        textures[4] = new Texture("textures/block_breaking/4.png");
    }

    public void delete() {
        for(int i = 0; i < 5; i++) {
            textures[i].delete();
        }
        glDeleteVertexArrays(this.vaoId);
        glDeleteBuffers(this.vboId);
    }

    public void render(Player.BlockBreakingProgress blockBreakingProgress) {
        if(blockBreakingProgress.getTotalBreakingTicks() == 0) {
            return;
        }
        
        glBindVertexArray(this.vaoId);

        SandboxGame.getInstance().getGameRenderer().skyRenderer.skyShader.use();
        SandboxGame.getInstance().getGameRenderer().skyRenderer.skyShader.uploadMatrix4f("view", SandboxGame.getInstance().getGameRenderer().camera.getViewMatrix());
        SandboxGame.getInstance().getGameRenderer().skyRenderer.skyShader.uploadMatrix4f("projection", SandboxGame.getInstance().getGameRenderer().camera.getProjectionMatrix());

        Matrix4f model = new Matrix4f();
        model.translate(blockBreakingProgress.blockPosition.x - 0.005F, blockBreakingProgress.blockPosition.y - 0.005F, blockBreakingProgress.blockPosition.z - 0.005F);

        SandboxGame.getInstance().getGameRenderer().skyRenderer.skyShader.uploadMatrix4f("model", model);

        textures[Math.clamp(blockBreakingProgress.breakingTicks / (blockBreakingProgress.getTotalBreakingTicks() / 5), 0, 4)].bind();

        glDrawArrays(GL_TRIANGLES, 0, this.vboLength);
    }
    
    public float[] generateCubeVertices() {
        SimpleVertexBuilder vertexBuilder = new SimpleVertexBuilder();
        // Top side
        vertexBuilder.vertex(1, 1, 1, 1, 0);
        vertexBuilder.vertex(1, 1, 0, 1, 1);
        vertexBuilder.vertex(0, 1, 0, 0, 1);

        vertexBuilder.vertex(0, 1, 0, 0, 1);
        vertexBuilder.vertex(0, 1, 1, 0, 0);
        vertexBuilder.vertex(1, 1, 1, 1, 0);    

        // Bottom side
        vertexBuilder.vertex(0, 0, 0, 0, 1);
        vertexBuilder.vertex(1, 0, 0, 1, 1);
        vertexBuilder.vertex(1, 0, 1, 1, 0);

        vertexBuilder.vertex(1, 0, 1, 1, 0);
        vertexBuilder.vertex(0, 0, 1, 0, 0);
        vertexBuilder.vertex(0, 0, 0, 0, 1);

        // Right side
        vertexBuilder.vertex(1, 0, 0, 1, 0);
        vertexBuilder.vertex(1, 1, 0, 1, 1);
        vertexBuilder.vertex(1, 1, 1, 0, 1);

        vertexBuilder.vertex(1, 1, 1, 0, 1);
        vertexBuilder.vertex(1, 0, 1, 0, 0);
        vertexBuilder.vertex(1, 0, 0, 1, 0);

        // Left side
        vertexBuilder.vertex(0, 1, 1, 1, 1);
        vertexBuilder.vertex(0, 1, 0, 0, 1);
        vertexBuilder.vertex(0, 0, 0, 0, 0);

        vertexBuilder.vertex(0, 0, 0, 0, 0);
        vertexBuilder.vertex(0, 0, 1, 1, 0);
        vertexBuilder.vertex(0, 1, 1, 1, 1);


        // Front side
        vertexBuilder.vertex(0, 0, 1, 0, 0);
        vertexBuilder.vertex(1, 0, 1, 1, 0);
        vertexBuilder.vertex(1, 1, 1, 1, 1);

        vertexBuilder.vertex(1, 1, 1, 1, 1);
        vertexBuilder.vertex(0, 1, 1, 0, 1);
        vertexBuilder.vertex(0, 0, 1, 0, 0);

        // Back side
        vertexBuilder.vertex(1, 1, 0, 0, 1);
        vertexBuilder.vertex(1, 0, 0, 0, 0);
        vertexBuilder.vertex(0, 0, 0, 1, 0);

        vertexBuilder.vertex(0, 0, 0, 1, 0);
        vertexBuilder.vertex(0, 1, 0, 1, 1);
        vertexBuilder.vertex(1, 1, 0, 0, 1);

        return vertexBuilder.compile(0,0,0);
    }
}
