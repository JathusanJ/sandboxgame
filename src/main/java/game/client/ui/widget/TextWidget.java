package game.client.ui.widget;

import game.client.ui.text.Text;
import org.joml.Vector2f;

public class TextWidget extends Widget {
    public Text text;
    public boolean centered = false;
    public float fontSize = 24F;

    public TextWidget(Text text, float fontSize, boolean centered) {
        this.text = text;
        this.centered = centered;
        this.fontSize = fontSize;
        this.size.y = fontSize;
    }

    public TextWidget(Text text) {
        this(text, 24F, false);
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        Vector2f position = this.position;

        if(this.centered) {
            position = new Vector2f(this.position.x + this.size.x / 2F, this.position.y + this.size.y / 2F);
        }

        this.gameRenderer.textRenderer.renderTextWithShadow(this.text.toString(), 24F, position.x, position.y, this.centered, this.centered);
    }
}
