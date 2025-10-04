package engine.input;

public class UnchangeableKeybind extends Keybind {
    public UnchangeableKeybind(String keybindId, int key) {
        super(keybindId, key);
    }

    @Override
    public void setKey(int key) {}
}
