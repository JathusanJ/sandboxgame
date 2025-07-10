package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.networking.GameClient;
import org.joml.Vector2f;

public class ServerWorldLoadingScreen extends Screen {
    public ServerWorldLoadingScreen() {

    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        if(this.gameRenderer.world != null && this.gameRenderer.world.ready && GameClient.state == GameClient.ClientState.PLAYING) {
            this.gameRenderer.setScreen(null);
            SandboxGame.getInstance().getWindow().captureCursor();
            return;
        }
        this.uiRenderer.renderTextWithShadow("Connecting to server", new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16), 32, true);
        this.uiRenderer.renderTextWithShadow("Loading world", new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 28, true);
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {

    }
}
