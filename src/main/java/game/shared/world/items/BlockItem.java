package game.shared.world.items;

import game.client.multiplayer.GameClient;
import game.client.multiplayer.GameClientHandler;
import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import game.shared.world.blocks.Block;
import game.server.world.ServerWorld;
import game.shared.world.blocks.Blocks;
import game.shared.world.creature.Creature;
import game.shared.multiplayer.packets.SetBlockPacket;
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
        } else {
            Block blockAtBlockPosition = context.world().getBlock(context.blockPosition());
            Block blockRightBelowThatBlock = context.world().getBlock(context.blockPosition().x, context.blockPosition().y - 1, context.blockPosition().z);

            Creature.Box playerHitbox = new Creature.Box(new Vector3f(context.player().position.x, context.player().position.y + context.player().size.y / 2F, context.player().position.z), context.player().size);

            if(blockAtBlockPosition != this.block && (blockAtBlockPosition.isEmpty() || blockAtBlockPosition.isReplaceable()) && this.block.canBePlacedOn(blockRightBelowThatBlock)) {
                if(this.block.hasCollision() && new Creature.Box(new Vector3f(context.blockPosition().x + 0.5F, context.blockPosition().y + 0.5F, context.blockPosition().z + 0.5F), new Vector3f(1F,1F,1F)).intersects(playerHitbox)) {
                    return;
                }
                if(!GameClient.isConnectedToServer) {
                    context.world().setBlock(context.blockPosition(), this.block);
                    context.itemStack().decreaseByUnlessInCreative(1, context.player());
                } else {
                    SetBlockPacket packet = new SetBlockPacket(context.blockPosition().x, context.blockPosition().y, context.blockPosition().z, this.block);
                    GameClientHandler.sendPacket(packet);
                }
                return;
            }
            Vector3i blockNextToBlockPosition = context.blockPosition().add(context.normal(), new Vector3i());
            Block blockNextToBlock = context.world().getBlock(blockNextToBlockPosition);
            Block blockRightBelowThatNexttoBlock = context.world().getBlock(blockNextToBlockPosition.x, blockNextToBlockPosition.y - 1, blockNextToBlockPosition.z);
            if(blockNextToBlock != this.block && (blockNextToBlock.isEmpty() || blockNextToBlock.isReplaceable()) && this.block.canBePlacedOn(blockRightBelowThatNexttoBlock)) {
                if(this.block.hasCollision() && new Creature.Box(new Vector3f(blockNextToBlockPosition.x + 0.5F, blockNextToBlockPosition.y + 0.5F, blockNextToBlockPosition.z + 0.5F), new Vector3f(1F,1F,1F)).intersects(playerHitbox)) {
                    return;
                }
                if(!GameClient.isConnectedToServer) {
                    context.world().setBlock(blockNextToBlockPosition, this.block);
                    context.itemStack().decreaseByUnlessInCreative(1, context.player());
                } else {
                    SetBlockPacket packet = new SetBlockPacket(blockNextToBlockPosition.x, blockNextToBlockPosition.y, blockNextToBlockPosition.z, this.block);
                    GameClientHandler.sendPacket(packet);
                }
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
