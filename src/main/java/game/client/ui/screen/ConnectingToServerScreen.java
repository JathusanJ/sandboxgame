package game.client.ui.screen;

import game.client.ui.widget.ButtonWidget;
import game.client.networking.GameClientHandler;
import org.joml.Vector2f;

public class ConnectingToServerScreen extends Screen {
    public int chosenSplash;
    public Screen parent;

    public ButtonWidget cancel = new ButtonWidget("Cancel", () -> {
        if(GameClientHandler.serverHandler != null) {
            GameClientHandler.serverHandler.close();
        }

        this.gameRenderer.setScreen(parent);
    });

    public ConnectingToServerScreen(Screen parent) {
        this.chosenSplash = (int) Math.floor(Math.random() * WorldLoadingScreen.splashes.length);
        this.parent = parent;
        this.renderableWidgets.add(this.cancel);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow("Connecting to server", new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16), 32, true);
        this.uiRenderer.renderTextWithShadow(WorldLoadingScreen.splashes[chosenSplash], new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 28, true);
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {
        this.cancel.size = new Vector2f(400, 50);
        this.cancel.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() / 2F - 160);
    }
}
