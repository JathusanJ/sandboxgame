package game.client.ui.widget;

import engine.input.Keybind;
import engine.input.KeyboardAndMouseInput;
import engine.input.UnchangeableKeybind;
import game.client.ui.text.Language;
import game.client.ui.text.Text;
import org.joml.Vector2f;

import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class KeybindWidget extends Widget {
    public Keybind keybind;
    public ButtonWidget rebindButton = new ButtonWidget("", () -> {
        this.awaitingKeyPress = true;
    });
    public boolean awaitingKeyPress = false;

    public KeybindWidget(Keybind keybind) {
        this.keybind = keybind;
        this.rebindButton.setText(new KeybindText(this.keybind, () -> awaitingKeyPress));

        if(keybind instanceof UnchangeableKeybind) {
            this.rebindButton.disabled = true;
        }
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderText(Language.translate("key." + this.keybind.keybindId), new Vector2f(this.position.x + 25F, this.position.y + this.size.y / 2F - 12F), 24F);

        this.rebindButton.position = new Vector2f(this.position.x + this.size.x - this.rebindButton.size.x - 25F, this.position.y);
        this.rebindButton.render(deltaTime, mouseX, mouseY);

        if(this.awaitingKeyPress && KeyboardAndMouseInput.lastKeyPressedLastFrame != 0) {
            this.keybind.key = KeyboardAndMouseInput.lastKeyPressedLastFrame;
            this.awaitingKeyPress = false;
        }
    }

    public static class KeybindText implements Text {
        public Keybind keybind;
        public Supplier<Boolean> isWaitingForKeyPress;

        public KeybindText(Keybind keybind, Supplier<Boolean> isWaitingForKeyPress) {
            this.keybind = keybind;
            this.isWaitingForKeyPress = isWaitingForKeyPress;
        }

        @Override
        public String toString() {
            if(this.isWaitingForKeyPress.get()) {
                return Language.translate("key.awaiting_key");
            }

            String keyName = glfwGetKeyName(this.keybind.key, 0);

            if(keyName == null) {
                // Hardcoded because it doesn't seem like GLFW has some all in one solution to get the text representation of every key
                // Also I hate it
                if(this.keybind.key == GLFW_KEY_SPACE) {
                    return "SPACE";
                } else if(this.keybind.key == GLFW_KEY_LEFT_SHIFT) {
                    return "LEFT SHIFT";
                } else if(this.keybind.key == GLFW_KEY_RIGHT_SHIFT) {
                    return "RIGHT SHIFT";
                } else if(this.keybind.key == GLFW_KEY_ESCAPE) {
                    return "ESCAPE";
                } else if(this.keybind.key == GLFW_KEY_TAB) {
                    return "TAB";
                } else if(this.keybind.key == GLFW_KEY_CAPS_LOCK) {
                    return "CAPS LOCK";
                } else if(this.keybind.key == GLFW_KEY_LEFT_ALT) {
                    return "LEFT ALT";
                } else if(this.keybind.key == GLFW_KEY_RIGHT_ALT) {
                    return "RIGHT ALT";
                } else if(this.keybind.key == GLFW_KEY_LEFT_CONTROL) {
                    return "LEFT CONTROL";
                } else if(this.keybind.key == GLFW_KEY_RIGHT_CONTROL) {
                    return "RIGHT CONTROL";
                } else if(this.keybind.key == GLFW_KEY_BACKSLASH) {
                    return "BACKSLASH";
                } else if(this.keybind.key == GLFW_KEY_BACKSPACE) {
                    return "BACKSPACE";
                } else if(this.keybind.key == GLFW_KEY_DELETE) {
                    return "DELETE";
                } else if(this.keybind.key == GLFW_KEY_HOME) {
                    return "HOME";
                } else if(this.keybind.key == GLFW_KEY_PAGE_DOWN) {
                    return "PAGE DOWN";
                } else if(this.keybind.key == GLFW_KEY_PAGE_UP) {
                    return "PAGE UP";
                }

                return "Unknown";
            }

            return keyName.toUpperCase();
        }
    }
}
