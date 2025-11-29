package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.LanguageListEntryWidget;
import game.client.ui.widget.ListWidget;
import org.joml.Vector2f;

public class LanguageSelectScreen extends Screen {
    public SettingsScreen prevScreen;
    public ButtonWidget closeButton = new ButtonWidget(new Text.Translated("ui.close"), () -> {
        this.gameRenderer.setScreen(this.prevScreen);
    });
    public ListWidget listWidget = new ListWidget();

    public LanguageSelectScreen(SettingsScreen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderableWidgets.add(closeButton);

        this.listWidget.widgets.add(new LanguageListEntryWidget("English", "en"));
        this.listWidget.widgets.add(new LanguageListEntryWidget("Deutsch", "de"));

        this.renderableWidgets.add(this.listWidget);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.language_select"), 32, 50, this.getScreenHeight() - 32 - 50);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prevScreen);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(50, 50);
        this.closeButton.size = new Vector2f(400F, 50F);
        this.listWidget.position = new Vector2f(50F, 150F);
        this.listWidget.size = new Vector2f(this.getScreenWidth() - 100F, this.getScreenHeight() - 100F - 50F - 32F - 50F - 50F);
    }
}
