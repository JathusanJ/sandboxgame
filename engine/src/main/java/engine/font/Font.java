package engine.font;

import engine.renderer.Texture;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public interface Font {
    Texture getFontTexture();
    float getWidth(String text);
    float getWidth(String text, float fontSize);
    FloatBuffer renderText(String text, float fontSize, float positionX, float positionY, Vector4f color, boolean centeredHorizontally, boolean centeredVertically);
}
