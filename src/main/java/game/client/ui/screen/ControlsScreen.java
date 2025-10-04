package game.client.ui.screen;

import engine.input.Keybind;
import game.client.Keybinds;
import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.*;
import org.joml.Vector2f;

import java.util.List;
import java.util.Map;

public class ControlsScreen extends Screen {
    public Screen prevScreen;
    public ButtonWidget closeButton = new ButtonWidget(new Text.Translated("ui.close"), this::close);

    public List<String> controls = List.of(
            Language.translate("ui.controls.movement") + " - W, A, S, D",
            Language.translate("ui.controls.inventory") + " - E",
            Language.translate("ui.controls.chat") + " - T",
            Language.translate("ui.controls.pause") + " - ESC",
            "",
            Language.translate("ui.controls.commands_only"),
            Language.translate("ui.controls.change_mode") + " - M",
            "",
            Language.translate("ui.controls.creative"),
            Language.translate("ui.controls.fly") + " - F"
    );

    public ListWidget keybindList = new ListWidget();

    public ControlsScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderableWidgets.add(closeButton);
        this.renderableWidgets.add(this.keybindList);

        for(Map.Entry<String, List<Keybind>> category : Keybinds.keybindCategories.entrySet()) {
            this.keybindList.widgets.add(new TextWidget(new Text.Translated("key.category." + category.getKey()), 24F, true));

            for(Keybind keybind : category.getValue()) {
                this.keybindList.widgets.add(new KeybindWidget(keybind));
            }
        }
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.controls"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);

    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prevScreen);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(50, 50);
        this.closeButton.size = new Vector2f(400F, 50F);
        this.keybindList.position = new Vector2f(50F, 150F);
        this.keybindList.size = new Vector2f(this.getScreenWidth() - 100F, this.getScreenHeight() - 100F - 50F - 32F - 50F - 50F);

        for(Widget widget : this.keybindList.widgets) {
            widget.size = new Vector2f(this.keybindList.size.x, 50F);

            if(widget instanceof KeybindWidget keybindWidget) {
                keybindWidget.rebindButton.size = new Vector2f(250F, widget.size.y);
            }
        }
    }
}
