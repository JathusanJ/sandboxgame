package game.client;

import engine.input.Keybind;

import static org.lwjgl.glfw.GLFW.*;

public class Keybinds {
    public static Keybind WALK_FORWARDS = new Keybind("key.walk_forwards", GLFW_KEY_W);
    public static Keybind WALK_BACKWARDS = new Keybind("key.walk_backwards", GLFW_KEY_S);
    public static Keybind WALK_RIGHT = new Keybind("key.walk_right", GLFW_KEY_D);
    public static Keybind WALK_LEFT = new Keybind("key.walk_left", GLFW_KEY_A);

    public static Keybind JUMP = new Keybind("key.jump", GLFW_KEY_SPACE);
    public static Keybind FLY_DOWN = new Keybind("key.fly_down", GLFW_KEY_LEFT_SHIFT);

    public static Keybind INVENTORY = new Keybind("key.inventory", GLFW_KEY_E);

    public static Keybind HOTBAR_SLOT_1 = new Keybind("key.hotbar_slot_1", GLFW_KEY_1);
    public static Keybind HOTBAR_SLOT_2 = new Keybind("key.hotbar_slot_2", GLFW_KEY_2);
    public static Keybind HOTBAR_SLOT_3 = new Keybind("key.hotbar_slot_3", GLFW_KEY_3);
    public static Keybind HOTBAR_SLOT_4 = new Keybind("key.hotbar_slot_4", GLFW_KEY_4);
    public static Keybind HOTBAR_SLOT_5 = new Keybind("key.hotbar_slot_5", GLFW_KEY_5);
    public static Keybind HOTBAR_SLOT_6 = new Keybind("key.hotbar_slot_6", GLFW_KEY_6);
    public static Keybind HOTBAR_SLOT_7 = new Keybind("key.hotbar_slot_7", GLFW_KEY_7);
    public static Keybind HOTBAR_SLOT_8 = new Keybind("key.hotbar_slot_8", GLFW_KEY_8);
    public static Keybind HOTBAR_SLOT_9 = new Keybind("key.hotbar_slot_9", GLFW_KEY_9);

    public static Keybind TOGGLE_FLIGHT = new Keybind("key.toggle_flight", GLFW_KEY_F);
    public static Keybind SWITCH_GAMEMODE = new Keybind("key.switch_gamemode", GLFW_KEY_M);

    public static Keybind OPEN_CHAT = new Keybind("key.open_chat", GLFW_KEY_T);
}
