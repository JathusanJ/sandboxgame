package game.logic.world.items;

import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import game.logic.world.blocks.Block;
import game.logic.world.ServerWorld;

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
            world.setBlockAt(context.blockPosition().x + context.normal().x, context.blockPosition().y + context.normal().y, context.blockPosition().z + context.normal().z, this.block);
        } else if(context.world() instanceof SingleplayerWorld) {
            if(context.world().getBlockAt(context.blockPosition().x + context.normal().x, context.blockPosition().y + context.normal().y, context.blockPosition().z + context.normal().z).isEmpty()) {
                context.world().setBlockAt(context.blockPosition().x + context.normal().x, context.blockPosition().y + context.normal().y, context.blockPosition().z + context.normal().z, this.block);
                context.itemStack().decreaseByUnlessInCreative(1, context.player());
            }
        }
    }

    @Override
    public String getName() {
        return Language.translate("block." + this.block.getBlockId());
    }
}
