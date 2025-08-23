package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.ButtonWidget;
import org.joml.Vector2f;

import java.util.List;

public class ControlsScreen extends Screen {
    public Screen prevScreen;
    public ButtonWidget closeButton = new ButtonWidget(new Text.Translated("ui.close"), this::close);

    public List<String> controls = List.of(
            Language.translate("ui.controls.movement") + " - W, A, S, D",
            Language.translate("ui.controls.inventory") + " - ESC",
            Language.translate("ui.controls.chat") + " - T",
            Language.translate("ui.controls.pause") + " - PAUSE",
            "",
            Language.translate("ui.controls.commands_only"),
            Language.translate("ui.controls.change_mode") + " - M",
            "",
            Language.translate("ui.controls.creative"),
            Language.translate("ui.controls.fly") + " - F"
    );

    public ControlsScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderableWidgets.add(closeButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.controls"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);

        int currentY = this.getScreenHeight() - 32 - 50;
        for(String line : controls) {
            currentY = currentY - 24;
            this.uiRenderer.renderTextWithShadow(line,  new Vector2f(50, currentY), 24);
        }
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prevScreen);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(50, 50);
        this.closeButton.size = new Vector2f(400F, 50F);
    }
}
