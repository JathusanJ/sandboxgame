package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.sound.Sounds;
import org.joml.Vector2f;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanToggleWidget extends Widget {
    public boolean disabled = false;
    private String text;
    private Consumer<Boolean> valueConsumer;
    private Supplier<Boolean> valueSupplier;

    public BooleanToggleWidget(String text, Consumer<Boolean> valueConsumer, Supplier<Boolean> valueSupplier) {
        this.text = text;
        this.valueConsumer = valueConsumer;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.disabled) {
            this.uiRenderer.renderTexture(ButtonWidget.BUTTON_DISABLED_TEXTURE, this.position, this.size);
        } else {
            if(mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y) {
                if(KeyboardAndMouseInput.hasLeftClicked()) {
                    Sounds.playSound("button_click.ogg");
                    this.valueConsumer.accept(!this.valueSupplier.get());
                }
                this.uiRenderer.renderTexture(ButtonWidget.BUTTON_SELECTED_TEXTURE, this.position, this.size);
            } else {
                this.uiRenderer.renderTexture(ButtonWidget.BUTTON_UNSELECTED_TEXTURE, this.position, this.size);
            }
        }

        this.uiRenderer.renderTextWithShadow(this.text + ": " + this.valueSupplier.get(), this.position.add(this.size.x / 2F, this.size.y / 2F - 12, new Vector2f()), 24, true);
    }
}
