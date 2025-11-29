package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.text.TextRenderer;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.ListWidget;
import game.client.ui.widget.TextWidget;
import org.joml.Vector2f;

import java.io.IOException;

public class LicenseViewerScreen extends Screen {
    public Screen prev;
    public String name;

    private ListWidget listWidget = new ListWidget();

    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);

    public LicenseViewerScreen(Screen prev, String name, String path) {
        this.prev = prev;
        this.name = name;

        String licenseContent;

        try {
            licenseContent = new String(Thread.currentThread().getContextClassLoader().getResourceAsStream("licenses/" + path).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(String line : licenseContent.lines().toList()) {
            this.listWidget.widgets.add(new TextWidget(new Text.Static(line), 24F, false, TextRenderer.UNIFONT));
        }

        this.renderableWidgets.add(listWidget);
        this.renderableWidgets.add(this.closeButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(this.name, 32, 50, this.getScreenHeight() - 32 - 50);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prev);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(this.getScreenWidth() - 200F - 50F, 50);
        this.closeButton.size = new Vector2f(200F, 50F);
        this.listWidget.position = new Vector2f(50F, 150F);
        this.listWidget.size = new Vector2f(this.getScreenWidth() - 100F, this.getScreenHeight() - 100F - 50F - 32F - 50F - 50F);
    }
}
