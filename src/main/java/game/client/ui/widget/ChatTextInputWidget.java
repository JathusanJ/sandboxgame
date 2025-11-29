package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;

public class ChatTextInputWidget extends TextFieldWidget {
    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.gameRenderer.getCurrentTextField() == this && (KeyboardAndMouseInput.pressedKey(GLFW_KEY_BACKSPACE)) && !content.isEmpty()) {
            content = content.substring(0, this.content.length() - 1);
        }

        this.gameRenderer.textRenderer.renderTextWithShadow(this.content, this.position.x + 10F, this.position.y + this.size.y / 2F, false, true);
    }
}
