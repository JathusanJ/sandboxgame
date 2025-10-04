package game.client;

import engine.input.Keybind;
import engine.input.UnchangeableKeybind;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

public class Keybinds {
    public static Map<String, List<Keybind>> keybindCategories = new LinkedHashMap<>();

    public static Keybind WALK_FORWARDS = register(new Keybind("walk_forwards", GLFW_KEY_W), "movement");
    public static Keybind WALK_LEFT = register(new Keybind("walk_left", GLFW_KEY_A), "movement");
    public static Keybind WALK_BACKWARDS = register(new Keybind("walk_backwards", GLFW_KEY_S), "movement");
    public static Keybind WALK_RIGHT = register(new Keybind("walk_right", GLFW_KEY_D), "movement");

    public static Keybind JUMP = register(new Keybind("jump", GLFW_KEY_SPACE), "movement");

    public static Keybind INVENTORY = register(new Keybind("inventory", GLFW_KEY_E), "inventory");

    public static Keybind HOTBAR_SLOT_1 = register(new Keybind("hotbar_slot_1", GLFW_KEY_1), "inventory");
    public static Keybind HOTBAR_SLOT_2 = register(new Keybind("hotbar_slot_2", GLFW_KEY_2), "inventory");
    public static Keybind HOTBAR_SLOT_3 = register(new Keybind("hotbar_slot_3", GLFW_KEY_3), "inventory");
    public static Keybind HOTBAR_SLOT_4 = register(new Keybind("hotbar_slot_4", GLFW_KEY_4), "inventory");
    public static Keybind HOTBAR_SLOT_5 = register(new Keybind("hotbar_slot_5", GLFW_KEY_5), "inventory");
    public static Keybind HOTBAR_SLOT_6 = register(new Keybind("hotbar_slot_6", GLFW_KEY_6), "inventory");
    public static Keybind HOTBAR_SLOT_7 = register(new Keybind("hotbar_slot_7", GLFW_KEY_7), "inventory");
    public static Keybind HOTBAR_SLOT_8 = register(new Keybind("hotbar_slot_8", GLFW_KEY_8), "inventory");
    public static Keybind HOTBAR_SLOT_9 = register(new Keybind("hotbar_slot_9", GLFW_KEY_9), "inventory");

    public static Keybind TOGGLE_FLIGHT = register(new Keybind("toggle_flight", GLFW_KEY_F), "commands_only");
    public static Keybind FLY_DOWN = register(new Keybind("fly_down", GLFW_KEY_LEFT_SHIFT), "commands_only");
    public static Keybind SWITCH_GAMEMODE = register(new Keybind("switch_gamemode", GLFW_KEY_M), "commands_only");

    public static Keybind OPEN_CHAT = register(new Keybind("open_chat", GLFW_KEY_T), "misc");
    public static Keybind PAUSE = register(new UnchangeableKeybind( "pause", GLFW_KEY_ESCAPE), "misc");

    public static Keybind register(Keybind keybind, String category) {
        if(!keybindCategories.containsKey(category)) {
            keybindCategories.put(category, new ArrayList<>());
        }

        keybindCategories.get(category).add(keybind);

        return keybind;
    }
}
