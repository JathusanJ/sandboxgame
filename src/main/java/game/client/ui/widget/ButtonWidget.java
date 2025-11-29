package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.NineSliceTexture;
import game.client.ui.text.TextRenderer;
import engine.renderer.Texture;
import engine.sound.Sounds;
import game.client.ui.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.nio.FloatBuffer;

public class ButtonWidget extends Widget {
    public boolean disabled = false;
    private Text text;
    private ButtonClickHandler buttonClickHandler;

    public static Texture BUTTON_UNSELECTED_TEXTURE = new NineSliceTexture("textures/ui/button_unselected.png", 1 / 25F, 1 / 25F, 0.01F, 0.01F);
    public static Texture BUTTON_SELECTED_TEXTURE = new NineSliceTexture("textures/ui/button_selected.png", 1 / 25F, 1 / 25F, 0.01F, 0.01F);
    public static Texture BUTTON_DISABLED_TEXTURE = new NineSliceTexture("textures/ui/button_disabled.png", 1 / 25F, 1 / 25F, 0.01F, 0.01F);

    public ButtonWidget(String text, ButtonClickHandler buttonClickHandler) {
        this.text = new Text.Static(text);
        this.buttonClickHandler = buttonClickHandler;
    }

    public ButtonWidget(Text text, ButtonClickHandler buttonClickHandler) {
        this.text = text;
        this.buttonClickHandler = buttonClickHandler;
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.disabled) {
            this.uiRenderer.renderTexture(BUTTON_DISABLED_TEXTURE, this.position, this.size);
        } else {
            if(mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y) {
                if(KeyboardAndMouseInput.hasLeftClicked()) {
                    Sounds.playSound("button_click.ogg");
                    this.buttonClickHandler.onClick();
                }
                this.uiRenderer.renderTexture(BUTTON_SELECTED_TEXTURE, this.position, this.size);
            } else {
                this.uiRenderer.renderTexture(BUTTON_UNSELECTED_TEXTURE, this.position, this.size);
            }
        }

        this.gameRenderer.textRenderer.renderTextWithShadow(this.text.toString(), 24F, this.position.x + this.size.x / 2F, this.position.y + this.size.y / 2F, true, true);
    }

    public void setText(String text) {
        this.text = new Text.Static(text);
    }

    public void setText(Text text) {
        this.text = text;
    }

    public interface ButtonClickHandler {
        void onClick();
    }
}
