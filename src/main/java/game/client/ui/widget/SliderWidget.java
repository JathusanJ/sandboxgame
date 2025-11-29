package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.NineSliceTexture;
import engine.renderer.Texture;
import engine.sound.Sounds;
import game.client.ui.text.Text;
import org.joml.Vector2f;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

public class SliderWidget extends Widget {
    public float value = 0F;
    public float maxValue = 100F;
    public float minValue = 0F;
    public float step = 1F;
    public Text label;
    public BiFunction<Float, Float, String> valueDisplayProcessor;
    public Consumer<Float> onValueChange;

    public boolean isDragging = false;

    public static Texture SLIDER_BACKGROUND_TEXTURE = new NineSliceTexture("textures/ui/slider_background.png", 1 / 25F, 1 / 25F, 0.01F, 0.01F);
    public static Texture SLIDER_BAR_SELECTED_TEXTURE = new NineSliceTexture("textures/ui/slider_bar_selected.png", 1 / 25F, 1 / 25F, 0.1F, 0.1F);
    public static Texture SLIDER_BAR_UNSELECTED_TEXTURE = new NineSliceTexture("textures/ui/slider_bar_unselected.png", 1 / 25F, 1 / 25F, 0.1F, 0.1F);

    public SliderWidget(Text label, BiFunction<Float, Float, String> valueDisplayProcessor, Consumer<Float> onValueChange) {
        this.size = new Vector2f(200F, 50F);
        this.label = label;
        this.valueDisplayProcessor = valueDisplayProcessor;
        this.onValueChange = onValueChange;
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.isDragging) {
            if(KeyboardAndMouseInput.pressingMouseButton(GLFW_MOUSE_BUTTON_1)) {
                this.value = this.minValue + (float) (Math.floor(Math.max((Math.min(mouseX - this.position.x, this.size.x) / this.size.x) * (this.maxValue - this.minValue), 0) / this.step) * this.step);
            } else {
                this.onValueChange.accept(this.value);
                this.isDragging = false;
            }
        }

        this.uiRenderer.renderTexture(SLIDER_BACKGROUND_TEXTURE, this.position, this.size);

        if(mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y) {
            if(KeyboardAndMouseInput.hasLeftClicked()){
                Sounds.playSound("button_click.ogg");
                this.isDragging = true;
            }

            this.uiRenderer.renderTexture(SLIDER_BAR_SELECTED_TEXTURE, this.position.add((this.value - this.minValue) / (this.maxValue - this.minValue) * (this.size.x - 20F), 0, new Vector2f()), new Vector2f(20, 50));
        } else {
            this.uiRenderer.renderTexture(SLIDER_BAR_UNSELECTED_TEXTURE, this.position.add((this.value - this.minValue) / (this.maxValue - this.minValue) * (this.size.x - 20F), 0, new Vector2f()), new Vector2f(20, 50));
        }

        this.gameRenderer.textRenderer.renderTextWithShadow(this.label + ": " + this.valueDisplayProcessor.apply(this.value, this.maxValue), this.position.x + this.size.x / 2F, this.position.y + this.size.y / 2F, true, true);
    }

    public static String displayValueAsPercentage(float value, float maxValue) {
        return String.valueOf(Math.floor((value / maxValue) * 100) / 100);
    }

    public static String displayValueAsIs(float value, float maxValue) {
        return String.valueOf(value);
    }
}
