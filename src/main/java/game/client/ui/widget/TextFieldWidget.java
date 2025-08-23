package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.Texture;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;

public class TextFieldWidget extends Widget {
    public String content = "";

    public static Texture TEXT_FIELD_UNSELECTED_BACKGROUND_TEXTURE = new Texture("textures/ui/slider_background.png");
    public static Texture TEXT_FIELD_SELECTED_BACKGROUND_TEXTURE = new Texture("textures/ui/border_selected.png");

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTexture(TEXT_FIELD_UNSELECTED_BACKGROUND_TEXTURE, this.position, this.size);
        if(this.gameRenderer.getCurrentTextField() == this) {
            this.uiRenderer.renderTexture(TEXT_FIELD_SELECTED_BACKGROUND_TEXTURE, this.position, this.size);
        }

        if(this.gameRenderer.getCurrentTextField() == this && (KeyboardAndMouseInput.pressedKey(GLFW_KEY_BACKSPACE)) && !content.isEmpty()) {
            content = content.substring(0, this.content.length() - 1);
        }

        this.uiRenderer.renderTextWithShadow(this.content, new Vector2f(this.position.x + 10F, this.position.y + this.size.y / 2F - 12F), 24);

        boolean mouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
        if(mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked()) {
            this.gameRenderer.setCurrentTextField(this);
        } else if(!mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked() && this.gameRenderer.getCurrentTextField() == this) {
            this.gameRenderer.setCurrentTextField(null);
        }
    }

    public void onCharacterInput(String characters) {
        content = content + characters;
    }
}
