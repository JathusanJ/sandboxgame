package game.client.rendering.creature;

import game.client.SandboxGame;
import game.client.rendering.chunk.SimpleVertexBuilder;
import game.client.ui.item.ItemTextures;
import game.logic.world.creature.ItemCreature;
import game.logic.world.items.BlockItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ItemCreatureVertexGenerator extends CreatureVertexGenerator<ItemCreature> {
    @Override
    public void render(ItemCreature creature, SimpleVertexBuilder vertexBuilder, double deltaTickTime) {
        float yOffset = (float) (0.25 + Math.sin(creature.ticksRemaining / 40F) * 0.25);

        Matrix4f rotation = new Matrix4f();
        rotation.rotate((float) (creature.lastPitch + (creature.pitch - creature.lastPitch) * deltaTickTime), new Vector3f(0,1,0));

        Vector4f corner1 = new Vector4f(-0.25F, yOffset, 0, 0).mul(rotation);
        Vector4f corner2 = new Vector4f(0.25F, yOffset, 0, 0).mul(rotation);
        Vector4f corner3 = new Vector4f(0.25F, yOffset + 0.5F, 0, 0).mul(rotation);
        Vector4f corner4 = new Vector4f(-0.25F, yOffset + 0.5F, 0, 0).mul(rotation);


        this.insertVertices(vertexBuilder, corner1, corner2, corner3, corner4);

        if(creature.representingItemStack.getItem() instanceof BlockItem blockItem && SandboxGame.getInstance().getGameRenderer().blockToItemIcon.containsKey(blockItem.getBlock())) {
            SandboxGame.getInstance().getGameRenderer().blockToItemIcon.get(blockItem.getBlock()).bind();
        } else {
            ItemTextures.getTexture(creature.representingItemStack.getItem().id).bind();
        }
    }
}
