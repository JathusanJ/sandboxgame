package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.multiplayer.GameClient;
import game.client.ui.text.Language;
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
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.connecting"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16), 32, true);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 28, true);
        if(GameClient.serverRenderDistance > 24) {
            this.uiRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading.while"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 40), 16, true);
        } else if(GameClient.serverRenderDistance > 8) {
            this.uiRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading.moment"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 40), 16, true);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {

    }
}
