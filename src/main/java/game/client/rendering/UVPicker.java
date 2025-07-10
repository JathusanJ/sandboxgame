package game.client.rendering;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class UVPicker {
    public static Vector4f uvOf(float textureWidth, float textureHeight, Vector2f position, float unitSize) {
        return new Vector4f(position.x * unitSize / textureWidth, position.y * unitSize / textureHeight, (position.x + 1) * unitSize / textureWidth, (position.y + 1) * unitSize / textureWidth);
    }
}
