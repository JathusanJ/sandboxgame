package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.ButtonWidget;
import game.client.multiplayer.GameClient;
import org.joml.Vector2f;

public class PauseMenuScreen extends Screen {
    public ButtonWidget resumeButton = new ButtonWidget(new Text.Translated("ui.screen.pause.resume"), this::close);
    public ButtonWidget quitButton = new ButtonWidget("", () -> {
        this.gameRenderer.chunkRenderer.uploadQueue.clear();
        SandboxGame.getInstance().doOnTickingThread(() -> {
            this.gameRenderer.unloadCurrentWorld();
        });
        this.gameRenderer.setScreen(new WorldSavingScreen());
    });
    public ButtonWidget settingsButton = new ButtonWidget(new Text.Translated("ui.screen.settings"), () -> {
        this.gameRenderer.setScreen(new SettingsScreen(this));
    });

    public PauseMenuScreen() {
        this.renderableWidgets.add(this.resumeButton);
        this.renderableWidgets.add(this.quitButton);
        this.renderableWidgets.add(this.settingsButton);

        if(GameClient.isConnectedToServer) {
            quitButton.setText(new Text.Translated("ui.screen.pause.disconnect"));
        } else {
            quitButton.setText(new Text.Translated("ui.screen.pause.quit"));
        }
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.pause.game_paused"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 175), 32, true);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(null);
        this.gameRenderer.world.shouldTick = true;
    }

    @Override
    public void positionContent() {
        this.resumeButton.size = new Vector2f(400, 50);
        this.resumeButton.position = new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() / 2F + 50);

        this.settingsButton.size = new Vector2f(400, 50);
        this.settingsButton.position = new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() / 2F - 25);

        this.quitButton.size = new Vector2f(400, 50);
        this.quitButton.position = new Vector2f(this.getScreenWidth() / 2F - 200, this.getScreenHeight() / 2F - 100);
    }
}
