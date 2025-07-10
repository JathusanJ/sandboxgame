package game.client.ui.screen;

import game.client.ui.widget.ButtonWidget;
import org.joml.Vector2f;

public class DisconnectedScreen extends Screen {
    public ButtonWidget done = new ButtonWidget("Done", () -> {
       this.gameRenderer.setScreen(new TitleScreen());
    });

    public String reason;

    public DisconnectedScreen(String reason) {
        this.renderableWidgets.add(this.done);
        this.reason = reason;
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow("Disconnected from server", new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 48), 32, true);
        this.uiRenderer.renderTextWithShadow(reason, new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 24, true);
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {
        this.done.size = new Vector2f(400, 50);
        this.done.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() / 2F - 160);
    }
}
