package game.client.ui.text;

import engine.font.Font;
import engine.renderer.Texture;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;

public class OldFont implements Font {
    public static Texture FONT_TEXTURE = new Texture("textures/font.png");

    private static final HashMap<String, Float> characterSize = new HashMap<>();
    private static char[] fontIndex = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.?!$:=+-[]()_;\",<>/\\#*%&'äüö|     ".toCharArray();

    public OldFont() {
        initialize();
    }

    public static void initialize(){
        if(!characterSize.isEmpty()) return;
        characterSize.put(" ", 8F);
        characterSize.put("a", 9F);
        characterSize.put("b", 10F);
        characterSize.put("c", 8F);
        characterSize.put("d", 10F);
        characterSize.put("e", 10F);
        characterSize.put("f", 7F);
        characterSize.put("g", 10F);
        characterSize.put("h", 9F);
        characterSize.put("i", 2F);
        characterSize.put("j", 5F);
        characterSize.put("k", 9F);
        characterSize.put("l", 2F);
        characterSize.put("m", 16F);
        characterSize.put("n", 9F);
        characterSize.put("o", 11F);
        characterSize.put("p", 10F);
        characterSize.put("q", 10F);
        characterSize.put("r", 6F);
        characterSize.put("s", 7F);
        characterSize.put("t", 7F);
        characterSize.put("u", 9F);
        characterSize.put("v", 10F);
        characterSize.put("w", 16F);
        characterSize.put("x", 10F);
        characterSize.put("y", 11F);
        characterSize.put("z", 7F);

        characterSize.put("A", 14F);
        characterSize.put("B", 10F);
        characterSize.put("C", 11F);
        characterSize.put("D", 12F);
        characterSize.put("E", 8F);
        characterSize.put("F", 8F);
        characterSize.put("G", 12F);
        characterSize.put("H", 12F);
        characterSize.put("I", 2F);
        characterSize.put("J", 7F);
        characterSize.put("K", 11F);
        characterSize.put("L", 8F);
        characterSize.put("M", 15F);
        characterSize.put("N", 12F);
        characterSize.put("O", 14F);
        characterSize.put("P", 10F);
        characterSize.put("Q", 15F);
        characterSize.put("R", 11F);
        characterSize.put("S", 10F);
        characterSize.put("T", 12F);
        characterSize.put("U", 11F);
        characterSize.put("V", 13F);
        characterSize.put("W", 16F);
        characterSize.put("X", 13F);
        characterSize.put("Y", 12F);
        characterSize.put("Z", 12F);

        characterSize.put("0", 10F);
        characterSize.put("1", 5F);
        characterSize.put("2", 9F);
        characterSize.put("3", 9F);
        characterSize.put("4", 11F);
        characterSize.put("5", 8F);
        characterSize.put("6", 10F);
        characterSize.put("7", 10F);
        characterSize.put("8", 10F);
        characterSize.put("9", 10F);
        characterSize.put(".", 3F);
        characterSize.put("?", 9F);
        characterSize.put("!", 3F);
        characterSize.put("$", 8F);
        characterSize.put(":", 3F);
        characterSize.put("=", 10F);
        characterSize.put("+", 10F);
        characterSize.put("-", 8F);
        characterSize.put("[", 4F);
        characterSize.put("]", 4F);
        characterSize.put("(", 4F);
        characterSize.put(")", 4F);
        characterSize.put("_", 10F);
        characterSize.put(";", 3F);
        characterSize.put("\"", 5F);
        characterSize.put(",", 3F);
        characterSize.put("<", 9F);
        characterSize.put(">", 9F);
        characterSize.put("/", 9F);
        characterSize.put("\\", 9F);
        characterSize.put("#", 11F);
        characterSize.put("*", 7F);
        characterSize.put("%", 15F);
        characterSize.put("&", 13F);
        characterSize.put("'", 2F);
        characterSize.put("ä", 9F);
        characterSize.put("ü", 9F);
        characterSize.put("ö", 11F);
        characterSize.put("|", 4F);
    }

    public static float getCharacterWidth(String character){
        return getCharacterWidth(character, 16F);
    }

    public static float getCharacterWidth(String character, float fontSize){
        return characterSize.getOrDefault(character, 16F) * fontSize / 16F;
    }

    public static float getTextWidth(String text){
        return getTextWidth(text, 16);
    }

    public static float getTextWidth(String text, float fontSize){
        float totalWidth = 0;

        for(int i = 0; i < text.length(); i++){
            totalWidth += 1.5F + getCharacterWidth(String.valueOf(text.charAt(i)));
        }

        return totalWidth / 16F * fontSize;
    }

    @Override
    public Texture getFontTexture() {
        return FONT_TEXTURE;
    }

    @Override
    public float getWidth(String text) {
        return getTextWidth(text);
    }

    @Override
    public float getWidth(String text, float fontSize) {
        return getTextWidth(text, fontSize);
    }

    private void writeQuad(FloatBuffer buffer, float positionX, float positionY, float width, float fontSize, Vector4f color, float u1, float v1, float u2, float v2) {
        // Top right
        buffer.put(positionX + width);
        buffer.put(positionY + fontSize);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(u2);
        buffer.put(v2);

        // Bottom right
        buffer.put(positionX + width);
        buffer.put(positionY);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(u2);
        buffer.put(v1);

        // Bottom left
        buffer.put(positionX);
        buffer.put(positionY);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(u1);
        buffer.put(v1);

        // Top left
        buffer.put(positionX);
        buffer.put(positionY + fontSize);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(u1);
        buffer.put(v2);
    }

    @Override
    public FloatBuffer renderText(String text, float fontSize, float positionX, float positionY, Vector4f color, boolean centeredHorizontally, boolean centeredVertically) {
        FloatBuffer data = BufferUtils.createFloatBuffer(text.length() * 4 * 9);

        if(centeredHorizontally) {
            positionX = positionX - this.getWidth(text, fontSize) / 2F;
        }

        if(centeredVertically) {
            positionY = positionY - fontSize / 2F;
        }

        for(int i = 0; i < text.length(); i++) {
            float characterWidth = getCharacterWidth(String.valueOf(text.charAt(i)), fontSize);

            int fontMapIndex = fontIndex.length - 1;
            for(int j = 0; j < fontIndex.length; j++){
                if(fontIndex[j] == text.charAt(i)){
                    fontMapIndex = j;
                    break;
                }
            }

            this.writeQuad(data, positionX, positionY, characterWidth, fontSize, color, (float) (fontMapIndex) / fontIndex.length,0, (float) (fontMapIndex + ((characterWidth) / fontSize)) / fontIndex.length,1);
            positionX = positionX + 1.5F * (fontSize / 16F) + characterWidth;
        }

        data.flip();

        return data;
    }
}
