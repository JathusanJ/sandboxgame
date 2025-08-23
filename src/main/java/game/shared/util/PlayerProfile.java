package game.shared.util;

import game.shared.multiplayer.skin.Skin;
import game.shared.multiplayer.skin.Skins;

import java.util.UUID;

public class PlayerProfile {
    private String username;
    private UUID accountUUID;
    private Skin skin = Skins.DEFAULT;

    public PlayerProfile(String username, UUID uuid) {
        this(username, uuid, Skins.DEFAULT);
    }

    public PlayerProfile(String username, UUID uuid, Skin skin) {
        this.username = username;
        this.accountUUID = uuid;
        this.skin = skin;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getAccountUUID() {
        return this.accountUUID;
    }

    public Skin getSkin() {
        return this.skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
