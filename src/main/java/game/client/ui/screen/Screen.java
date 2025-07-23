package game.client.ui.screen;

import game.client.SandboxGame;
import engine.input.KeyboardAndMouseInput;
import game.client.rendering.renderer.GameRenderer;
import engine.renderer.Texture;
import game.client.rendering.renderer.UIRenderer;
import game.client.ui.widget.Widget;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public abstract class Screen {
    public GameRenderer gameRenderer = SandboxGame.getInstance().getGameRenderer();
    public UIRenderer uiRenderer = this.gameRenderer.uiRenderer;
    public SandboxGame gameClient = SandboxGame.getInstance();

    public List<Widget> renderableWidgets = new ArrayList<>();

    public static Texture PAUSE_BACKGROUND_TEXTURE = new Texture("textures/ui/pause_background.png");

    public void renderBackground(double deltaTime, int mouseX, int mouseY) {
        if(this.gameRenderer.world != null && this.gameRenderer.world.ready) {
            this.uiRenderer.renderTexture(PAUSE_BACKGROUND_TEXTURE, new Vector2f(), new Vector2f(this.gameClient.getWindow().width, this.gameClient.getWindow().height));
        } else {
            TitleScreen.backgroundScroll = (TitleScreen.backgroundScroll + (float) deltaTime / 60F) % 1;
            this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 * -TitleScreen.backgroundScroll, 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
            this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 - (this.getScreenHeight() * 4 * TitleScreen.backgroundScroll), 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
        }
    }

    public void render(double deltaTime, int mouseX, int mouseY) {
        this.renderBackground(deltaTime, mouseX, mouseY);

        for(Widget widget : this.renderableWidgets) {
            widget.render(deltaTime, mouseX, mouseY);
        }

        this.renderContents(deltaTime, mouseX, mouseY);

        if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_ESCAPE) || KeyboardAndMouseInput.pressedKey(GLFW_KEY_E)) {
            this.close();
        }
    }

    public int getScreenWidth() {
        return SandboxGame.getInstance().getWindow().getWindowWidth();
    }

    public int getScreenHeight() {
        return SandboxGame.getInstance().getWindow().getWindowHeight();
    }

    public void onScroll(double scroll, float mouseX, float mouseY) {
        for(Widget widget : this.renderableWidgets) {
            if(mouseX > widget.position.x && mouseX < widget.position.x + widget.size.x && mouseY > widget.position.y && mouseY < widget.position.y + widget.size.y) {
                widget.onScroll(scroll, mouseX, mouseY);
            }
        }
    }

    public abstract void renderContents(double deltaTime, int mouseX, int mouseY);
    public abstract void close();
    public abstract void positionContent();
}
