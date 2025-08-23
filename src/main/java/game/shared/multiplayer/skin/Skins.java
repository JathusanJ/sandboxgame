package game.shared.multiplayer.skin;

import java.util.HashMap;

public class Skins {
    public static HashMap<String, Skin> idToSkin = new HashMap<>();

    public static Skin DEFAULT = register("default");
    public static Skin OAK_TREE = register("oak_tree");
    public static Skin MISSING = register("missing");

    public static Skin register(String id) {
        Skin skin = new Skin(id);
        idToSkin.put(id, skin);

        return skin;
    }

    public static Skin getSkin(String id) {
        return idToSkin.getOrDefault(id, Skins.DEFAULT);
    }

    public static void loadTextures() {
        for(Skin skin : idToSkin.values()) {
            skin.loadTexture();
        }
    }
}
