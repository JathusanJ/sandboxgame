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
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.connecting"), 32, this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16, true);
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading"), 28, this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24, true);
        if(GameClient.serverRenderDistance > 24) {
            this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading.while"), 16, this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 40, true);
        } else if(GameClient.serverRenderDistance > 8) {
            this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.downloading.moment"), 16, this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 40, true);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {

    }
}
