package game.client.rendering.renderer;

import engine.renderer.NineSliceTexture;
import game.client.SandboxGame;
import engine.renderer.Camera2D;
import engine.renderer.Shader;
import engine.renderer.Texture;
import game.client.ui.text.OldFont;
import game.client.ui.widget.Tooltip;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

// Copied over from Flappy Mario's source code and modified to suit this game
public class UIRenderer {
    private int vboId;
    private int vaoId;
    private int eboId;

    public Shader texturedQuadShader;
    private Shader coloredQuadShader;

    public Camera2D camera = new Camera2D();

    private final int maxQuadCount = 512;

    private int[] indices = generateIndices(maxQuadCount);

    private ArrayList<Tooltip> tooltipsToBeRendered = new ArrayList<>();

    private int[] generateIndices(int maxQuadCount) {
        int[] indicesArray = new int[maxQuadCount * 6];

        for(int i = 0; i < maxQuadCount; i++){
            int offset = i * 6;
            int vertexOffset = i * 4;

            indicesArray[offset] = vertexOffset;
            indicesArray[offset + 1] = vertexOffset + 1;
            indicesArray[offset + 2] = vertexOffset + 3;

            indicesArray[offset + 3] = vertexOffset + 1;
            indicesArray[offset + 4] = vertexOffset + 2;
            indicesArray[offset + 5] = vertexOffset + 3;
        }

        return indicesArray;
    }

    public void setup() {
        this.vboId = glGenBuffers();

        this.vaoId = glGenVertexArrays();
        glBindVertexArray(this.vaoId);

        this.eboId = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferData(GL_ARRAY_BUFFER, (long) maxQuadCount * 4 * 9 * Float.BYTES, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, (long) this.indices.length * Float.BYTES, GL_STATIC_DRAW);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indices);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 9 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 9 * Float.BYTES, 7 * Float.BYTES);
        glEnableVertexAttribArray(2);

        this.texturedQuadShader = new Shader("shaders/textured_quad.vertex.glsl", "shaders/textured_quad.fragment.glsl");
        this.coloredQuadShader = new Shader("shaders/colored_quad.vertex.glsl", "shaders/colored_quad.fragment.glsl");

        this.camera.orthographicProjection(SandboxGame.getInstance().getWindow().getWindowWidth(), SandboxGame.getInstance().getWindow().getWindowHeight());
    }

    public void delete() {
        this.texturedQuadShader.delete();
        this.coloredQuadShader.delete();
        glDeleteVertexArrays(this.vaoId);
        glDeleteBuffers(this.vboId);
        glDeleteBuffers(this.eboId);
    }

    public Matrix4f createModelMatrix(Vector2f position, Vector2f size, float rotation) {
        Matrix4f model = new Matrix4f().translate(position.x, position.y, 0.0F);

        if(rotation != 0) {
            // "Move" the origin so that the rotation is in the center and not in a corner of the texture
            model = model.translate(0.5F * size.x, 0.5F * size.y, 0.0F);
            model = model.rotate((float) Math.toRadians(rotation), 0.0F, 0.0F, 1.0F);
            model = model.translate(-0.5F * size.x, -0.5F * size.y, 0.0F);
        }

        model = model.scale(size.x, size.y, 1.0F);

        return model;
    }

    public void renderTexture(Texture texture, Vector2f position, Vector2f size, float rotation) {
        this.renderTextureInternal(texture, position, size, this.createModelMatrix(position, size, rotation));
    }

    public void renderTexture(Texture texture, Vector2f position, Vector2f size) {
        this.renderTexture(texture, position, size, 0.0F);
    }

    public void renderTexture(Texture texture, Vector2f position, Vector2f size, Vector4f uv) {
        this.renderTextureInternal(texture, position, size, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector2f(uv.x, uv.y), new Vector2f(uv.z, uv.w), this.createModelMatrix(position, size, 0F));
    }

    private void renderTextureInternal(Texture texture, Vector2f position, Vector2f size, Matrix4f model) {
        this.renderTextureInternal(texture, position, size, new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector2f(), new Vector2f(1.0F, 1.0F), model);
    }

    private void renderTextureInternal(Texture texture, Vector2f position, Vector2f size, Vector4f color, Vector2f textureCoordinatesBottomLeft, Vector2f textureCoordinatesTopRight, Matrix4f model) {
        if(texture instanceof NineSliceTexture nineSliceTexture) {
            float[] vertexData = new float[9 * 4 * 9];
            // Left
            this.setVertexDataForQuad(vertexData, 0, position, new Vector2f(nineSliceTexture.left * texture.getWidth(),  nineSliceTexture.bottom * texture.getHeight()), color, new Vector2f(0, 0), new Vector2f(nineSliceTexture.left, nineSliceTexture.bottom));
            this.setVertexDataForQuad(vertexData, 4 * 9, new Vector2f(position.x, position.y + nineSliceTexture.bottom * texture.getHeight()), new Vector2f(nineSliceTexture.left * texture.getWidth(),  size.y - nineSliceTexture.bottom * texture.getHeight() - nineSliceTexture.top * texture.getHeight()), color, new Vector2f(0, nineSliceTexture.bottom), new Vector2f(nineSliceTexture.left, 1 - nineSliceTexture.top));
            this.setVertexDataForQuad(vertexData, 2 * 4 * 9, new Vector2f(position.x, position.y + size.y - nineSliceTexture.top * texture.getHeight()), new Vector2f(nineSliceTexture.left * texture.getWidth(),  nineSliceTexture.top * texture.getHeight()), color, new Vector2f(0, 1 - nineSliceTexture.top), new Vector2f(nineSliceTexture.left, 1));

            // Middle
            this.setVertexDataForQuad(vertexData, 3 * 4 * 9, new Vector2f(position.x + nineSliceTexture.left * texture.getWidth(), position.y), new Vector2f(size.x - nineSliceTexture.left * texture.getWidth() - nineSliceTexture.right * texture.getWidth(),  nineSliceTexture.bottom * texture.getHeight()), color, new Vector2f(nineSliceTexture.left, 0), new Vector2f(1 - nineSliceTexture.right, nineSliceTexture.bottom));
            this.setVertexDataForQuad(vertexData, 4 * 4 * 9, new Vector2f(position.x + nineSliceTexture.left * texture.getWidth(), position.y + nineSliceTexture.bottom * texture.getHeight()), new Vector2f(size.x - nineSliceTexture.left * texture.getWidth() - nineSliceTexture.right * texture.getWidth(),  size.y - nineSliceTexture.bottom * texture.getHeight() - nineSliceTexture.top * texture.getHeight()), color, new Vector2f(nineSliceTexture.left, nineSliceTexture.bottom), new Vector2f(1 - nineSliceTexture.right, 1 - nineSliceTexture.top));
            this.setVertexDataForQuad(vertexData, 5 * 4 * 9, new Vector2f(position.x + nineSliceTexture.left * texture.getWidth(), position.y + size.y - nineSliceTexture.top * texture.getHeight()), new Vector2f(size.x - nineSliceTexture.left * texture.getWidth() - nineSliceTexture.right * texture.getWidth(),  nineSliceTexture.top * texture.getHeight()), color, new Vector2f(nineSliceTexture.left, 1 - nineSliceTexture.top), new Vector2f(1 - nineSliceTexture.right, 1));

            // Right
            this.setVertexDataForQuad(vertexData, 6 * 4 * 9, new Vector2f(position.x + size.x - nineSliceTexture.right * texture.getWidth(), position.y), new Vector2f(nineSliceTexture.right * texture.getWidth(),  nineSliceTexture.bottom * texture.getHeight()), color, new Vector2f(1 - nineSliceTexture.right, 0), new Vector2f(1, nineSliceTexture.bottom));
            this.setVertexDataForQuad(vertexData, 7 * 4 * 9, new Vector2f(position.x + size.x - nineSliceTexture.right * texture.getWidth(), position.y + nineSliceTexture.bottom * texture.getHeight()), new Vector2f(nineSliceTexture.right * texture.getWidth(),  size.y - nineSliceTexture.bottom * texture.getHeight() - nineSliceTexture.top * texture.getHeight()), color, new Vector2f(1 - nineSliceTexture.right, nineSliceTexture.bottom), new Vector2f(1, 1 - nineSliceTexture.top));
            this.setVertexDataForQuad(vertexData, 8 * 4 * 9, new Vector2f(position.x + size.x - nineSliceTexture.right * texture.getWidth(), position.y + size.y - nineSliceTexture.top * texture.getHeight()), new Vector2f(nineSliceTexture.right * texture.getWidth(),  nineSliceTexture.top * texture.getHeight()), color, new Vector2f(1 - nineSliceTexture.right, 1 - nineSliceTexture.top), new Vector2f(1, 1));

            this.draw(vertexData, texture, new Matrix4f(), vertexData.length / 36);
        } else {
            float[] vertexData = new float[4 * 9];
            this.setVertexDataForQuad(vertexData, 0, new Vector2f(), new Vector2f(1F, 1F), color, textureCoordinatesBottomLeft, textureCoordinatesTopRight);

            this.draw(vertexData, texture, model);
        }
    }

    private void setVertexDataForQuad(float[] vertexData, int offset, Vector2f position, Vector2f size, Vector4f color, Vector2f textureCoordinatesBottomLeft, Vector2f textureCoordinatesTopRight) {
        // top right
        // position
        vertexData[offset] = position.x + size.x;
        vertexData[offset + 1] = position.y + size.y;
        vertexData[offset + 2] = 0.0F;
        // color
        vertexData[offset + 3] = color.x;
        vertexData[offset + 4] = color.y;
        vertexData[offset + 5] = color.z;
        vertexData[offset + 6] = color.w;
        // texture coordinates
        vertexData[offset + 7] = textureCoordinatesTopRight.x;
        vertexData[offset + 8] = textureCoordinatesTopRight.y;

        // bottom right
        // position
        vertexData[offset + 9] = position.x + size.x;
        vertexData[offset + 10] = position.y;
        vertexData[offset + 11] = 0.0F;
        // color
        vertexData[offset + 12] = color.x;
        vertexData[offset + 13] = color.y;
        vertexData[offset + 14] = color.z;
        vertexData[offset + 15] = color.w;
        // texture coordinates
        vertexData[offset + 16] = textureCoordinatesTopRight.x;
        vertexData[offset + 17] = textureCoordinatesBottomLeft.y;

        // bottom left
        // position
        vertexData[offset + 18] = position.x;
        vertexData[offset + 19] = position.y;
        vertexData[offset + 20] = 0.0F;
        // color
        vertexData[offset + 21] = color.x;
        vertexData[offset + 22] = color.y;
        vertexData[offset + 23] = color.z;
        vertexData[offset + 24] = color.w;
        // texture coordinates
        vertexData[offset + 25] = textureCoordinatesBottomLeft.x;
        vertexData[offset + 26] = textureCoordinatesBottomLeft.y;

        // top left
        // position
        vertexData[offset + 27] = position.x;
        vertexData[offset + 28] = position.y + size.y;
        vertexData[offset + 29] = 0.0F;
        // color
        vertexData[offset + 30] = color.x;
        vertexData[offset + 31] = color.y;
        vertexData[offset + 32] = color.z;
        vertexData[offset + 33] = color.w;
        // texture coordinates
        vertexData[offset + 34] = textureCoordinatesBottomLeft.x;
        vertexData[offset + 35] = textureCoordinatesTopRight.y;
    }

    private void draw(float[] vertexData, Texture texture, Matrix4f model) {
        this.draw(vertexData, texture, model, 1);
    }

    private void draw(float[] vertexData, Texture texture, Matrix4f model, int quadsAmount) {
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexData);

        this.texturedQuadShader.use();
        this.texturedQuadShader.uploadMatrix4f("projection", this.camera.getProjectionMatrix());
        this.texturedQuadShader.uploadMatrix4f("model", model);
        this.texturedQuadShader.uploadMatrix4f("view", this.camera.getViewMatrix());
        texture.bind();
        if(SandboxGame.getInstance().getPlayerProfile().getUsername().equals("SliceMyUIInHalf")) {
            this.drawBuffer(quadsAmount * 3);
        } else {
            this.drawBuffer(quadsAmount * 6);
        }
    }

    public void draw(FloatBuffer vertexData, Texture texture, Matrix4f model, int quadsAmount) {
        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexData);

        this.texturedQuadShader.use();
        this.texturedQuadShader.uploadMatrix4f("projection", this.camera.getProjectionMatrix());
        this.texturedQuadShader.uploadMatrix4f("model", model);
        this.texturedQuadShader.uploadMatrix4f("view", this.camera.getViewMatrix());
        texture.bind();
        if(SandboxGame.getInstance().getPlayerProfile().getUsername().equals("SliceMyUIInHalf")) {
            this.drawBuffer(quadsAmount * 3);
        } else {
            this.drawBuffer(quadsAmount * 6);
        }
    }

    private void drawBuffer(int vertexAmount) {
        glBindVertexArray(this.vaoId);
        glDrawElements(GL_TRIANGLES, vertexAmount, GL_UNSIGNED_INT, 0);
    }

    @Deprecated
    public void renderTextWithShadow(String text, Vector2f position, float fontSize, Vector4f color, boolean centeredHorizontally) {
        this.renderText(text, position.add(fontSize / 12F, -fontSize / 12F, new Vector2f()), fontSize, new Vector4f(0, 0, 0, 1), centeredHorizontally);
        this.renderText(text, position, fontSize, color, centeredHorizontally);
    }

    @Deprecated
    public void renderTextWithShadow(String text, Vector2f position, float fontSize, Vector4f color) {
        this.renderTextWithShadow(text, position, fontSize, color, false);
    }

    @Deprecated
    public void renderTextWithShadow(String text, Vector2f position, float fontSize) {
        this.renderTextWithShadow(text, position, fontSize, new Vector4f(1, 1, 1, 1));
    }

    @Deprecated
    public void renderTextWithShadowRightSided(String text, Vector2f position, float fontSize) {
        this.renderTextWithShadow(text, position.sub(SandboxGame.getInstance().getGameRenderer().textRenderer.getWidth(text, fontSize), 0), fontSize, new Vector4f(1, 1, 1, 1));
    }

    @Deprecated
    public void renderTextWithShadow(String text, Vector2f position, float fontSize, boolean centeredHorizontally) {
        this.renderTextWithShadow(text, position, fontSize, new Vector4f(1, 1, 1, 1), centeredHorizontally);
    }

    @Deprecated
    public void renderText(String text, Vector2f position, float fontSize){
        this.renderText(text, position, fontSize, false);
    }

    @Deprecated
    public void renderText(String text, Vector2f position, float fontSize, Vector4f color){
        this.renderText(text, position, fontSize, color, false);
    }

    @Deprecated
    public void renderText(String text, Vector2f position, float fontSize, boolean centeredHorizontally){
        this.renderText(text, position, fontSize, new Vector4f(1,1,1,1), centeredHorizontally, 0);
    }

    @Deprecated
    public void renderText(String text, Vector2f position, float fontSize, Vector4f color, boolean centeredHorizontally){
        this.renderText(text, position, fontSize, color, centeredHorizontally, 0);
    }

    @Deprecated
    public void renderText(String text, Vector2f position, float fontSize, Vector4f color, boolean centeredHorizontally, float rotation){
        SandboxGame.getInstance().getGameRenderer().textRenderer.renderText(text, fontSize, position.x, position.y, color, centeredHorizontally);
    }

    public void addTooltipToBeRendered(Tooltip tooltip) {
        this.tooltipsToBeRendered.add(tooltip);
    }

    public void renderTooltip(Tooltip tooltip) {
        float sizeY = tooltip.getContent().size() * 24;
        float sizeX = 0;

        for(String line : tooltip.getContent()) {
            float lineTextWidth = SandboxGame.getInstance().getGameRenderer().textRenderer.getWidth(line);
            if(lineTextWidth > sizeX) sizeX = lineTextWidth;
        }

        this.renderColoredQuad(new Vector2f(tooltip.position.x - 4, tooltip.position.y - 4), new Vector2f(sizeX + 8, sizeY + 8), new Vector4f(0F, 0F, 0F, 0.5F));

        float y = tooltip.position.y;

        for(String line : tooltip.getContent()) {
            SandboxGame.getInstance().getGameRenderer().textRenderer.renderTextWithShadow(line, tooltip.position.x, y);
            y = y - 24;
        }
    }

    public void renderAllTooltips() {
        for(Tooltip tooltip : this.tooltipsToBeRendered) {
            this.renderTooltip(tooltip);
        }

        this.tooltipsToBeRendered.clear();
    }

    public void renderColoredQuad(Vector2f position, Vector2f size, Vector4f color) {
        this.renderColoredQuad(position, size, color, 0);
    }

    public void renderColoredQuad(Vector2f position, Vector2f size, Vector4f color, float rotation) {
        float[] vertexData = new float[4 * 9];
        this.setVertexDataForQuad(vertexData, 0, new Vector2f(0, 0), new Vector2f(1, 1), color, new Vector2f(), new Vector2f(1,1));

        glBindBuffer(GL_ARRAY_BUFFER, this.vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexData);

        this.coloredQuadShader.use();
        this.coloredQuadShader.uploadMatrix4f("projection", this.camera.getProjectionMatrix());
        this.coloredQuadShader.uploadMatrix4f("model", this.createModelMatrix(position, size, rotation));
        this.coloredQuadShader.uploadMatrix4f("view", this.camera.getViewMatrix());

        this.drawBuffer(6);
    }
}
