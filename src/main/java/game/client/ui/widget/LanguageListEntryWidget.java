package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import game.client.ui.text.Language;
import org.joml.Vector2f;

import static game.client.ui.widget.WorldWidget.BORDER_SELECTED_TEXTURE;

public class LanguageListEntryWidget extends Widget {
    public String name;
    public String id;

    public LanguageListEntryWidget(String name, String id) {
        this.name = name;
        this.id = id;
        this.size = new Vector2f(400F, 50F);
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(this.name, this.position.x + 10F, this.position.y + this.size.y / 2F, false, true);

        boolean mouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
        if(mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked()) {
            Language.load(this.id);
        }

        if(this.client.settings.language.equals(this.id)) {
            this.uiRenderer.renderTexture(BORDER_SELECTED_TEXTURE, this.position, this.size);
        }
    }
}
