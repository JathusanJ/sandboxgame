package engine.input;

public class Keybind {
    public String keybindId;
    public int key;

    public Keybind(String keybindId, int key) {
        this.keybindId = keybindId;
        this.key = key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean pressed() {
        return KeyboardAndMouseInput.pressedKey(key);
    }

    public boolean pressing() {
        return KeyboardAndMouseInput.pressingKey(key);
    }
}
