package game.client.rendering.chunk;

import game.client.rendering.UVPicker;
import game.logic.world.blocks.Block;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;

public class SimpleVertexBuilder {
    public ArrayList<VertexData> data = new ArrayList<>();

    public void vertex(float x, float y, float z, float u, float v) {
        this.data.add(new VertexData(new Vector3f(x, y, z), new Vector2f(u, v)));
    }

    public void clear() {
        this.data.clear();
    }

    public float[] compile(float offsetX, float offsetY, float offsetZ) {
        float[] vertices = new float[this.data.size() * 5];

        for (int i = 0; i < this.data.size(); i++) {
            VertexData vertexData = this.data.get(i);
            int offset = i * 5;
            vertices[offset] = vertexData.position.x + offsetX;
            vertices[offset + 1] = vertexData.position.y + offsetY;
            vertices[offset + 2] = vertexData.position.z + offsetZ;
            vertices[offset + 3] = vertexData.textureCoordinates.x;
            vertices[offset + 4] = vertexData.textureCoordinates.y;
        }

        return vertices;
    }

    public record VertexData(Vector3f position, Vector2f textureCoordinates) {}
}
