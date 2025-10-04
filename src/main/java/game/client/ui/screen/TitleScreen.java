package game.client.ui.screen;

import game.client.SandboxGame;
import engine.renderer.Texture;
import game.client.ui.text.Text;
import game.client.ui.widget.ButtonWidget;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class TitleScreen extends Screen {
    public ButtonWidget singleplayerButton = new ButtonWidget(new Text.Translated("ui.screen.singleplayer"), () -> {
        this.gameRenderer.setScreen(new WorldSelectScreen(this));
    });
    public ButtonWidget closeGameButton = new ButtonWidget(new Text.Translated("ui.screen.title_screen.close_game"), this::close);

    public ButtonWidget multiplayerButton = new ButtonWidget(new Text.Translated("ui.screen.multiplayer"), () -> {
        this.gameRenderer.setScreen(new MultiplayerScreen());
    });

    public ButtonWidget settingsButton = new ButtonWidget(new Text.Translated("ui.screen.settings"), () -> {
        this.gameRenderer.setScreen(new SettingsScreen(this));
    });

    public static Texture BACKGROUND_TEXTURE = new Texture("textures/titlescreen_background.png");

    public static float backgroundScroll = 0F;

    public TitleScreen() {
        this.renderableWidgets.add(this.singleplayerButton);
        this.renderableWidgets.add(this.closeGameButton);
        this.renderableWidgets.add(this.multiplayerButton);
        this.renderableWidgets.add(this.settingsButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow("Sandbox Game", new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() - 124), 48, true);
        this.uiRenderer.renderTextWithShadow("Sandbox Game " + SandboxGame.getInstance().getVersion().versionName(), new Vector2f(0, this.getScreenHeight() - 24), 24);
    }

    @Override
    public void close() {
        GLFW.glfwSetWindowShouldClose(SandboxGame.getInstance().getWindow().getGlfwWindow(), true);
    }

    @Override
    public void positionContent() {
        this.singleplayerButton.setPosition(new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() - 224));
        this.singleplayerButton.setSize(new Vector2f(400, 50));

        this.multiplayerButton.setPosition(new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() - 224 - 75));
        this.multiplayerButton.setSize(new Vector2f(400, 50));

        this.settingsButton.setPosition(new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() - 224 - 75 * 2));
        this.settingsButton.setSize(new Vector2f(400, 50));

        this.closeGameButton.setPosition(new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() - 224 - 75 * 3));
        this.closeGameButton.setSize(new Vector2f(400, 50));
    }
}
