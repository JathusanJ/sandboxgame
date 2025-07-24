package game.logic.world.items;

import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import game.logic.world.blocks.Block;
import game.logic.world.ServerWorld;
import game.logic.world.blocks.Blocks;
import game.logic.world.creature.Creature;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class BlockItem extends Item {
    private Block block;
    private String itemTextureOverride;

    public BlockItem(Block block) {
        this.block = block;
    }

    public BlockItem(Block block, String itemTextureOverride) {
        this.block = block;
        this.itemTextureOverride = itemTextureOverride;
    }


    public Block getBlock() {
        return this.block;
    }

    @Override
    public void onUse(ItemUsageContext context) {
        if(this.block == Blocks.AIR || context.blockPosition() == null || context.normal() == null) return;

        if(context.world() instanceof ServerWorld world) {
            world.setBlock(context.blockPosition().x + context.normal().x, context.blockPosition().y + context.normal().y, context.blockPosition().z + context.normal().z, this.block);
        } else if(context.world() instanceof SingleplayerWorld) {
            Block blockAtBlockPosition = context.world().getBlock(context.blockPosition());
            Block blockRightBelowThatBlock = context.world().getBlock(context.blockPosition().x, context.blockPosition().y - 1, context.blockPosition().z);

            Creature.Box playerHitbox = new Creature.Box(new Vector3f(context.player().position.x, context.player().position.y + context.player().size.y / 2F, context.player().position.z + 0.5F), context.player().size);

            if(blockAtBlockPosition != this.block && (blockAtBlockPosition.isEmpty() || blockAtBlockPosition.isReplaceable()) && this.block.canBePlacedOn(blockRightBelowThatBlock)) {
                if(this.block.hasCollision() && new Creature.Box(new Vector3f(context.blockPosition().x + 0.5F, context.blockPosition().y + 0.5F, context.blockPosition().z + 0.5F), new Vector3f(1F,1F,1F)).intersects(playerHitbox)) {
                    return;
                }
                context.world().setBlock(context.blockPosition(), this.block);
                context.itemStack().decreaseByUnlessInCreative(1, context.player());
                return;
            }
            Vector3i blockNextToBlockPosition = context.blockPosition().add(context.normal(), new Vector3i());
            Block blockNextToBlock = context.world().getBlock(blockNextToBlockPosition);
            Block blockRightBelowThatNexttoBlock = context.world().getBlock(blockNextToBlockPosition.x, blockNextToBlockPosition.y - 1, blockNextToBlockPosition.z);
            if(blockNextToBlock != this.block && (blockNextToBlock.isEmpty() || blockNextToBlock.isReplaceable()) && this.block.canBePlacedOn(blockRightBelowThatNexttoBlock)) {
                if(this.block.hasCollision() && new Creature.Box(new Vector3f(blockNextToBlockPosition.x + 0.5F, blockNextToBlockPosition.y + 0.5F, blockNextToBlockPosition.z + 0.5F), new Vector3f(1F,1F,1F)).intersects(playerHitbox)) {
                    return;
                }
                context.world().setBlock(blockNextToBlockPosition, this.block);
                context.itemStack().decreaseByUnlessInCreative(1, context.player());
            }
        }
    }

    @Override
    public String getName() {
        return Language.translate("block." + this.block.getBlockId());
    }

    public String getOverriddenItemTexture() {
        return this.itemTextureOverride;
    }
}
