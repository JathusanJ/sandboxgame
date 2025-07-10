package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import engine.renderer.Texture;
import game.client.ui.widget.ButtonWidget;
import game.client.networking.GameClient;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class TitleScreen extends Screen {
    public ButtonWidget singleplayerButton = new ButtonWidget(Language.translate("ui.screen.singleplayer"), () -> {
        this.gameRenderer.setScreen(new WorldSelectScreen(this));
    });
    public ButtonWidget closeGameButton = new ButtonWidget(Language.translate("ui.screen.title_screen.close_game"), this::close);

    public ButtonWidget multiplayerButton = new ButtonWidget(Language.translate("ui.screen.multiplayer"), () -> {
        this.gameRenderer.setScreen(new ConnectingToServerScreen(this));
        Thread networkingThread = new Thread(() -> {
            try {
                GameClient.connect("localhost", 8080);
            } catch (InterruptedException e) {
                this.gameRenderer.setScreen(new DisconnectedScreen(e.getMessage()));
            }}, "client-network");

        networkingThread.start();
    });

    public ButtonWidget settingsButton = new ButtonWidget(Language.translate("ui.screen.settings"), () -> {
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
