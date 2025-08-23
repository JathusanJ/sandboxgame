package game.shared.world.creature;

import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.skin.Skin;
import game.shared.multiplayer.skin.Skins;
import io.netty.buffer.ByteBuf;

public class OtherPlayer extends Player {
    public Skin skin;

    @Override
    public void readSpawnPacket(ByteBuf buffer) {
        super.readSpawnPacket(buffer);
        this.skin = Skins.getSkin(ByteBufPacketDecoder.readString(buffer));
    }

    @Override
    public Skin getSkin() {
        return skin;
    }
}
