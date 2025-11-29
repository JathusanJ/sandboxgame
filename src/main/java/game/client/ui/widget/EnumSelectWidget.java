package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.sound.Sounds;
import org.joml.Vector2f;

public class EnumSelectWidget<E extends Enum> extends Widget {
    public boolean disabled = false;
    private String text;
    private Class<E> enumType;
    private int currentlySelected = 0;

    public EnumSelectWidget(String text, Class<E> enumType) {
        this.text = text;
        this.enumType = enumType;

    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.disabled) {
            this.uiRenderer.renderTexture(ButtonWidget.BUTTON_DISABLED_TEXTURE, this.position, this.size);
        } else {
            if(mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y) {
                if(KeyboardAndMouseInput.hasLeftClicked()) {
                    Sounds.playSound("button_click.ogg");
                    this.currentlySelected++;
                    if(this.currentlySelected >= this.enumType.getEnumConstants().length) {
                        this.currentlySelected = 0;
                    }
                }
                this.uiRenderer.renderTexture(ButtonWidget.BUTTON_SELECTED_TEXTURE, this.position, this.size);
            } else {
                this.uiRenderer.renderTexture(ButtonWidget.BUTTON_UNSELECTED_TEXTURE, this.position, this.size);
            }
        }

        this.gameRenderer.textRenderer.renderTextWithShadow(this.text + ": " + this.enumType.getEnumConstants()[this.currentlySelected].toString(), this.position.x + this.size.x / 2F, this.position.y + this.size.y / 2F, true);
    }

    public E getValue() {
        return this.enumType.getEnumConstants()[this.currentlySelected];
    }
}
