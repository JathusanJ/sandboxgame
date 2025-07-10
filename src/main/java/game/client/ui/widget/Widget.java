package game.client.ui.widget;

import game.client.SandboxGame;
import game.client.rendering.renderer.GameRenderer;
import game.client.rendering.renderer.UIRenderer;
import org.joml.Vector2f;

public abstract class Widget {
    public GameRenderer gameRenderer = SandboxGame.getInstance().getGameRenderer();
    public UIRenderer uiRenderer = this.gameRenderer.uiRenderer;
    public SandboxGame gameClient = SandboxGame.getInstance();

    public Vector2f position = new Vector2f();
    public Vector2f size = new Vector2f();

    public abstract void render(double deltaTime, int mouseX, int mouseY);

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setSize(Vector2f size) {
        this.size = size;
    }

    public void onScroll(double scroll, float mouseX, float mouseY) {}
}
