package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.ButtonWidget;
import org.joml.Vector2f;

public class CreditsScreen extends Screen {
    public String text = """
            !ui.credits.developed_by
            
            #!ui.credits.textures
            Jathusan Jeyabalachandran
            Mikhael Rocha Oliveira
            Leana Vinzens
            """;

    public Screen prevScreen;
    public ButtonWidget closeButton = new ButtonWidget(new Text.Translated("ui.close"), this::close);

    public CreditsScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderableWidgets.add(this.closeButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.credits"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
        int currentY = this.getScreenHeight() - 32 - 50;

        for(String line : text.lines().toList()) {
            int fontSize = 24;
            String content = line;
            if(content.startsWith("#")) {
                currentY = currentY - 32;
                fontSize = 32;
                content = content.substring(1);
            } else {
                currentY = currentY - 24;
            }

            if(content.startsWith("!")) {
                content = Language.translate(content.substring(1));
            }

            this.uiRenderer.renderTextWithShadow(content, new Vector2f(50, currentY), fontSize);
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
