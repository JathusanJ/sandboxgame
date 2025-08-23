package game.shared.multiplayer.skin;

import engine.renderer.Texture;

public class Skin {
    public Texture texture;
    public String id;

    public Skin(String id) {
        this.id = id;
    }

    public void loadTexture() {
        this.texture = new Texture("textures/skins/" + id + ".png");
    }

    public String getTranslationId() {
        return "skin." + id;
    }
}
