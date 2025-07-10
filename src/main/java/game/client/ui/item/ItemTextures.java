package game.client.ui.item;

import engine.renderer.Texture;

import java.util.HashMap;

public class ItemTextures {
    public static String itemTexturesFolderPath = "textures/items/";

    public static HashMap<String, Texture> textures = new HashMap<>();

    public static void initialize() {
        textures.put("missing", new Texture(itemTexturesFolderPath + "missing.png"));
    }

    public static Texture getTexture(String itemId) {
        if(textures.get(itemId) != null) {
            return textures.get(itemId);
        } else {
            System.out.println("Attempting to load assets/" + itemTexturesFolderPath + itemId + ".png");
            if(Thread.currentThread().getContextClassLoader().getResource("assets/" + itemTexturesFolderPath + itemId + ".png") != null) {
                textures.put(itemId, new Texture("textures/items/" + itemId + ".png"));
                return textures.get(itemId);
            } else {
                if(textures.get("missing") != null) {
                    textures.put(itemId, textures.get("missing"));

                    return textures.get("missing");
                } else {
                    throw new IllegalStateException("Missing item texture missing (quite ironic)");
                }
            }
        }
    }
}
