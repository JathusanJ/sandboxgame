package engine.input;

import engine.GameEngine;
import org.joml.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardAndMouseInput {
    public static boolean[] keysPressing = new boolean[GLFW_KEY_LAST];
    public static boolean[] keyPressedLastFrame = new boolean[GLFW_KEY_LAST];
    public static boolean[] pressingMouseButton = new boolean[GLFW_MOUSE_BUTTON_LAST];
    public static boolean[] pressedMouseButtonLastFrame = new boolean[GLFW_MOUSE_BUTTON_LAST];
    public static boolean[] keysRepeating = new boolean[GLFW_KEY_LAST];
    public static Double lastMouseX = null;
    public static Double lastMouseY = null;
    public static int lastKeyPressedLastFrame = 0;
    public static int lastKeyPressedThisFrame = 0;

    public static void onKeyCallback(long window, int key, int scanCode, int action, int mods){
        if(key < 0 || key >= GLFW_KEY_LAST) return;

        if(action == GLFW_PRESS){
            keysPressing[key] = true;
            lastKeyPressedThisFrame = key;
        } else if(action == GLFW_RELEASE){
            keysPressing[key] = false;
            keysRepeating[key] = false;
        } else if(action == GLFW_REPEAT) {
            keysRepeating[key] = true;
        }
    }

    public static void onMouseButtonCallback(long glfwWindow, int key, int action, int mods) {
        if(action == GLFW_PRESS) {
            pressingMouseButton[key] = true;
            lastKeyPressedThisFrame = key;
        } else {
            pressingMouseButton[key] = false;
        }
    }

    public static boolean pressingKey(int keyCode){
        return keysPressing[keyCode];
    }

    public static boolean pressedKey(int keyCode){
        return keysPressing[keyCode] && !keyPressedLastFrame[keyCode];
    }

    public static boolean pressingMouseButton(int keyCode) {
        return pressingMouseButton[keyCode];
    }

    public static void onCursorPosCallback(long glfwWindow, double x, double y) {
        if(lastMouseX == null || lastMouseY == null) {
            lastMouseX = x;
            lastMouseY = y;
        }

        double offsetX = x - lastMouseX;
        double offsetY = lastMouseY - y;

        lastMouseX = x;
        lastMouseY = y;

        GameEngine.getGame().onMouseMovement(offsetX, offsetY);
    }

    public static void onCursorScrollCallback(long glfwWindow, double xScroll, double yScroll) {
            if(Math.abs(yScroll) > 0.5) {
                GameEngine.getGame().onMouseScroll(xScroll, yScroll);

            }
    }

    public static void updateLastFramePressed() {
        keyPressedLastFrame = keysPressing.clone();
        pressedMouseButtonLastFrame = pressingMouseButton.clone();
        lastKeyPressedLastFrame = lastKeyPressedThisFrame;
        lastKeyPressedThisFrame = 0;
    }

    public static Vector2i getMousePosition() {
        return new Vector2i(lastMouseX == null ? 0 : lastMouseX.intValue(), lastMouseY == null ? 0 : GameEngine.getWindow().height - lastMouseY.intValue());
    }

    public static boolean pressedMouseButton(int buttonCode) {
        return pressingMouseButton[buttonCode] && !pressedMouseButtonLastFrame[buttonCode];
    }

    public static boolean hasLeftClicked() {
        return pressedMouseButton(GLFW_MOUSE_BUTTON_1);
    }

    public static boolean hasRightClicked() {
        return pressedMouseButton(GLFW_MOUSE_BUTTON_2);
    }

    public static void onCharacterInput(long glfwWindow, int codePoint) {
        String character = Character.toString(codePoint);
        GameEngine.getGame().onCharacterInput(character);
    }

    public static boolean isKeyRepeating(int key) {
        return keysRepeating[key];
    }
}
