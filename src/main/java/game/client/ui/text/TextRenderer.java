package game.client.ui.text;

import engine.font.Font;
import engine.font.TrueTypeFont;
import game.client.rendering.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.nio.FloatBuffer;

public class TextRenderer {
    public static Font UNIFONT = new TrueTypeFont("unifont.otf", 24);
    public static Font ROBOTO = new TrueTypeFont("Roboto-Medium.ttf", 48);
    public static Font SANDBOX_FONT = new OldFont();

    public Font currentFont = SANDBOX_FONT;
    public float defaultFontSize = 24F;
    public GameRenderer gameRenderer;

    public TextRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
    }

    public float getWidth(Text text) {
        return this.getWidth(text.toString());
    }

    public float getWidth(Text text, float fontSize) {
        return this.getWidth(text.toString(), fontSize);
    }

    public float getWidth(String text) {
        return this.getWidth(text, this.defaultFontSize);
    }

    public float getWidth(String text, float fontSize) {
        return this.currentFont.getWidth(text, fontSize);
    }

    public void renderText(String text, float fontSize, float x, float y) {
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), false, false);
    }

    public void renderTextWithShadow(String text, float fontSize, float x, float y) {
        this.renderText(this.currentFont, text, fontSize, x + 2 * fontSize / 24F, y - 2 * fontSize / 24F, new Vector4f(0,0,0,1), false, false);
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), false, false);
    }

    public void renderTextWithShadow(String text, float x, float y) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x + 2 * this.defaultFontSize / 24F, y - 2 * this.defaultFontSize / 24F, new Vector4f(0,0,0,1), false, false);
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, new Vector4f(1,1,1,1), false, false);
    }

    public void renderTextWithShadow(String text, float x, float y, Vector4f color) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x + 2 * this.defaultFontSize / 24F, y - 2 * this.defaultFontSize / 24F, new Vector4f(0,0,0,1), false, false);
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, color, false, false);
    }

    public void renderTextWithShadow(String text, float x, float y, boolean centeredHorizontally) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x + 2 * this.defaultFontSize / 24F, y - 2 * this.defaultFontSize / 24F, new Vector4f(0,0,0,1), centeredHorizontally, false);
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, false);
    }

    public void renderTextWithShadow(String text, float x, float y, Vector4f color, boolean centeredHorizontally, boolean centeredVertically) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x + 2 * this.defaultFontSize / 24F, y - 2 * this.defaultFontSize / 24F, color, centeredHorizontally, centeredVertically);
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, centeredVertically);
    }

    public void renderTextWithShadow(String text, float x, float y, boolean centeredHorizontally, boolean centeredVertically) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x + 2 * this.defaultFontSize / 24F, y - 2 * this.defaultFontSize / 24F, new Vector4f(0,0,0,1), centeredHorizontally, centeredVertically);
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, centeredVertically);
    }

    public void renderText(String text, float fontSize, float x, float y, boolean centeredHorizontally) {
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, false);
    }

    public void renderText(String text, float fontSize, float x, float y, Vector4f color, boolean centeredHorizontally) {
        this.renderText(this.currentFont, text, fontSize, x, y, color, centeredHorizontally, false);
    }

    public void renderTextWithShadow(String text, float fontSize, float x, float y, boolean centeredHorizontally) {
        this.renderText(this.currentFont, text, fontSize, x + 2 * fontSize / 24F, y - 2 * fontSize / 24F, new Vector4f(0,0,0,1), centeredHorizontally, false);
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, false);
    }

    public void renderText(String text, float fontSize, float x, float y, boolean centeredHorizontally, boolean centeredVertically) {
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, centeredVertically);
    }

    public void renderText(String text, float x, float y, Vector4f color, boolean centeredHorizontally, boolean centeredVertically) {
        this.renderText(this.currentFont, text, this.defaultFontSize, x, y, color, centeredHorizontally, centeredVertically);
    }

    public void renderTextWithShadow(String text, float fontSize, float x, float y, boolean centeredHorizontally, boolean centeredVertically) {
        this.renderText(this.currentFont, text, fontSize, x + 2 * fontSize / 24F, y - 2 * fontSize / 24F, new Vector4f(0,0,0,1), centeredHorizontally, centeredVertically);
        this.renderText(this.currentFont, text, fontSize, x, y, new Vector4f(1,1,1,1), centeredHorizontally, centeredVertically);
    }

    public void renderText(Font font, String text, float fontSize, float x, float y, Vector4f color, boolean centeredHorizontally, boolean centeredVertically) {
        FloatBuffer data = font.renderText(text, fontSize, x, y, color, centeredHorizontally, centeredVertically);
        this.gameRenderer.uiRenderer.draw(data, font.getFontTexture(), new Matrix4f(), text.length());
    }
}
