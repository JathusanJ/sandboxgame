package game.logic.world.items;

import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import game.logic.world.blocks.Block;
import game.logic.world.ServerWorld;
import org.joml.Vector3i;

public class BlockItem extends Item {
    private Block block;

    public BlockItem(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public void onUse(ItemUsageContext context) {
        if(context.blockPosition() == null || context.normal() == null) return;

        if(context.world() instanceof ServerWorld world) {
            world.setBlock(context.blockPosition().x + context.normal().x, context.blockPosition().y + context.normal().y, context.blockPosition().z + context.normal().z, this.block);
        } else if(context.world() instanceof SingleplayerWorld) {
            Block blockAtBlockPosition = context.world().getBlock(context.blockPosition());
            if(blockAtBlockPosition.isEmpty() || blockAtBlockPosition.isReplaceable()) {
                context.world().setBlock(context.blockPosition(), this.block);
                context.itemStack().decreaseByUnlessInCreative(1, context.player());
                return;
            }
            Vector3i blockNextToBlockPosition = context.blockPosition().add(context.normal(), new Vector3i());
            Block blockNextToBlock = context.world().getBlock(blockNextToBlockPosition);
            if(blockNextToBlock.isEmpty() || blockNextToBlock.isReplaceable()) {
                context.world().setBlock(blockNextToBlockPosition, this.block);
                context.itemStack().decreaseByUnlessInCreative(1, context.player());
            }
        }
    }

    @Override
    public String getName() {
        return Language.translate("block." + this.block.getBlockId());
    }
}
