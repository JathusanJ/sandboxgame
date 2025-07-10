package game.client.rendering.creature;

import game.client.rendering.chunk.SimpleVertexBuilder;
import game.client.ui.item.ItemTextures;
import game.logic.world.creature.OtherPlayer;
import game.logic.world.creature.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PlayerVertexGenerator extends CreatureVertexGenerator<Player> {
    @Override
    public void render(Player creature, SimpleVertexBuilder vertexBuilder, double deltaTickTime) {
        if(!(creature instanceof OtherPlayer)) return;

        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float) ((float) (creature.yaw + (creature.yaw - creature.lastYaw) * deltaTickTime) / 180 * Math.PI), new Vector3f(0,1,0));

        Vector4f corner1 = new Vector4f(-0.25F, 0, 0, 0).mul(rotation);
        Vector4f corner2 = new Vector4f(0.25F, 0, 0, 0).mul(rotation);
        Vector4f corner3 = new Vector4f(0.25F, creature.size.y, 0, 0).mul(rotation);
        Vector4f corner4 = new Vector4f(-0.25F, creature.size.y, 0, 0).mul(rotation);

        this.insertVertices(vertexBuilder, corner1, corner2, corner3, corner4);

        ItemTextures.getTexture("missing").bind();
    }
}
