package engine.font;

import engine.renderer.Texture;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;

public class TrueTypeFont implements Font {
    public Texture fontTexture;

    public int textureWidth;
    public int textureHeight;
    public int fontPixelHeight;
    public int characterAmount;
    public STBTTBakedChar.Buffer charData;
    public STBTTFontinfo fontInfo;
    // The bytebuffer used with stbtt_InitFont needs to be stored
    // From reply 1 of http://forum.lwjgl.org/index.php?topic=6917.0
    // This quirk kinda made me crazy ngl
    // That's the fun of lower level programming I guess
    public ByteBuffer fileContents;

    public TrueTypeFont(String name, int fontPixelHeight) {
        this(name, fontPixelHeight, 4096, 4096, Short.MAX_VALUE * 2);
    }

    public TrueTypeFont(String name, int fontPixelHeight, int textureWidth, int textureHeight, int characterAmount) {
        this.fontPixelHeight = fontPixelHeight;
        this.fontInfo = STBTTFontinfo.create();
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.characterAmount = characterAmount;

        try {
            byte[] data = Thread.currentThread().getContextClassLoader().getResourceAsStream("font/" + name).readAllBytes();
            fileContents = BufferUtils.createByteBuffer(data.length);
            fileContents.put(data);
            fileContents.flip();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        STBTruetype.stbtt_InitFont(fontInfo, fileContents);
        ByteBuffer pixels = BufferUtils.createByteBuffer(this.textureWidth * this.textureHeight);
        this.charData = STBTTBakedChar.create(this.characterAmount);
        STBTruetype.stbtt_BakeFontBitmap(fileContents, this.fontPixelHeight, pixels, this.textureWidth, this.textureHeight, 0, this.charData);

        // Make it white instead of black
        // Is there a better way? Don't really like this lol
        ByteBuffer finalPixels = BufferUtils.createByteBuffer(this.textureWidth * this.textureHeight * 4);
        for (int i = 0; i < (this.textureWidth * this.textureHeight); i++) {
            byte data = pixels.get();
            finalPixels.put(data);
            finalPixels.put(data);
            finalPixels.put(data);
            finalPixels.put(data);
        }
        finalPixels.flip();

        this.fontTexture = new Texture(GL11.glGenTextures());
        this.fontTexture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL_RGBA, this.textureWidth, this.textureHeight, 0, GL_RGBA, GL11.GL_UNSIGNED_BYTE, finalPixels);
    }

    @Override
    public Texture getFontTexture() {
        return this.fontTexture;
    }

    public float getWidth(String text) {
        return this.getWidth(text, 24F);
    }

    public float getWidth(String text, float fontSize) {
        float length = 0;

        IntBuffer advance = BufferUtils.createIntBuffer(1);
        IntBuffer a = BufferUtils.createIntBuffer(1);

        for(int i = 0; i < text.length(); i++) {
            advance.position(0);
            a.position(0);
            STBTruetype.stbtt_GetCodepointHMetrics(this.fontInfo, text.codePointAt(i), advance, a);

            length = length + advance.get();
        }

        return length * STBTruetype.stbtt_ScaleForPixelHeight(this.fontInfo, fontSize);
    }

    private void writeQuad(FloatBuffer buffer, float scale, float positionX, float positionY, Vector4f color, STBTTAlignedQuad alignedQuad) {
        // Top right
        buffer.put(positionX + alignedQuad.x1() * scale);
        buffer.put(positionY - alignedQuad.y0() * scale);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(alignedQuad.s1());
        buffer.put(alignedQuad.t0());

        // Bottom right
        buffer.put(positionX + alignedQuad.x1() * scale);
        buffer.put(positionY - alignedQuad.y1() * scale);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(alignedQuad.s1());
        buffer.put(alignedQuad.t1());

        // Bottom left
        buffer.put(positionX + alignedQuad.x0() * scale);
        buffer.put(positionY - alignedQuad.y1() * scale);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(alignedQuad.s0());
        buffer.put(alignedQuad.t1());

        // Top left
        buffer.put(positionX + alignedQuad.x0() * scale);
        buffer.put(positionY - alignedQuad.y0() * scale);
        buffer.put(0F);

        buffer.put(color.x);
        buffer.put(color.y);
        buffer.put(color.z);
        buffer.put(color.w);

        buffer.put(alignedQuad.s0());
        buffer.put(alignedQuad.t0());
    }

    @Override
    public FloatBuffer renderText(String text, float fontSize, float positionX, float positionY, Vector4f color, boolean centeredHorizontally, boolean centeredVertically) {
        FloatBuffer x = BufferUtils.createFloatBuffer(1); // stb_truetype adds to the value present in the buffer, it does not set it
        FloatBuffer y = BufferUtils.createFloatBuffer(1);
        STBTTAlignedQuad alignedQuad = STBTTAlignedQuad.create();

        FloatBuffer data = BufferUtils.createFloatBuffer(text.length() * 4 * 9);

        float xOffset = 0;
        float yOffset = 0;

        if(centeredHorizontally) {
            xOffset = -this.getWidth(text, fontSize) / 2F;
        }

        if(centeredVertically) {
            yOffset = -fontSize / 4F;
        }

        for (int i = 0; i < text.length(); i++) {
            STBTruetype.stbtt_GetBakedQuad(this.charData, this.textureWidth, this.textureHeight, text.codePointAt(i), x, y, alignedQuad, true);
            this.writeQuad(data, fontSize / this.fontPixelHeight, positionX + xOffset, positionY + yOffset, color, alignedQuad);
        }

        data.flip();

        return data;
    }
}
