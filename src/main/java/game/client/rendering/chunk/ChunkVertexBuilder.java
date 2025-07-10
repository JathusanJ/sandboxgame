package game.client.rendering.chunk;

import game.client.rendering.UVPicker;
import game.logic.world.blocks.Block;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class ChunkVertexBuilder {
    public ArrayList<VertexData> data = new ArrayList<>();

    public void vertex(float x, float y, float z, float u, float v, Normal normal, Block block) {
        this.data.add(new VertexData(new Vector3f(x, y, z), new Vector2f(u, v), normal, block, 0F, 1F));
    }

    public void vertex(float x, float y, float z, float u, float v, Normal normal, Block block, float skylight, float light) {
        this.data.add(new VertexData(new Vector3f(x, y, z), new Vector2f(u, v), normal, block, skylight, light));
    }

    public void clear() {
        this.data.clear();
    }

    public float[] compile(float offsetX, float offsetY, float offsetZ) {
        float[] vertices = new float[this.data.size() * 8];

        for (int i = 0; i < this.data.size(); i++) {
            VertexData vertexData = this.data.get(i);
            int offset = i * 8;

            Vector4f uv = UVPicker.uvOf(160, 160, vertexData.block.getTextures()[vertexData.normal.ordinal()], 16);

            vertices[offset] = vertexData.position.x + offsetX;
            vertices[offset + 1] = vertexData.position.y + offsetY;
            vertices[offset + 2] = vertexData.position.z + offsetZ;
            vertices[offset + 3] = uv.x + vertexData.textureCoordinates.x * 16 / 160;
            vertices[offset + 4] = uv.y + vertexData.textureCoordinates.y * 16 / 160;
            vertices[offset + 5] = vertexData.normal.getNormalLighting();
            vertices[offset + 6] = vertexData.skylight;
            vertices[offset + 7] = vertexData.light;
        }

        return vertices;
    }

    public record VertexData(Vector3f position, Vector2f textureCoordinates, Normal normal, Block block, float skylight, float light) {}

    public enum Normal {
        TOP(new Vector3f(0, 1, 0), 1F),
        BOTTOM(new Vector3f(0, -1, 0), 0.25F),
        RIGHT(new Vector3f(1, 0, 0), 0.5F),
        LEFT(new Vector3f(-1, 0 ,0), 0.5F),
        FRONT(new Vector3f(0, 0, 1), 0.75F),
        BACK(new Vector3f(0, 0, -1), 0.75F);

        Vector3f normal;
        float normalLighting;

        Normal(Vector3f normal, float normalLighting) {
            this.normal = normal;
            this.normalLighting = normalLighting;
        }

        public Vector3f getValue() {
            return this.normal;
        }

        public float getNormalLighting() {
            return this.normalLighting;
        }
    }
}
