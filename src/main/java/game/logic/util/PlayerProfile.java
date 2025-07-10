package game.logic.util;

import java.util.UUID;

public class PlayerProfile {
    private String username;
    private UUID accountUUID;

    public PlayerProfile(String username, UUID accountUUID) {
        this.username = username;
        this.accountUUID = accountUUID;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getAccountUUID() {
        return this.accountUUID;
    }
}
