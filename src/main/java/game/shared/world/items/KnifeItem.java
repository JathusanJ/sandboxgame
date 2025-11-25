package game.shared.world.items;

import game.client.multiplayer.GameClient;
import game.shared.world.blocks.Blocks;

public class KnifeItem extends Item {
    @Override
    public void onUse(ItemUsageContext context) {
        if(GameClient.isConnectedToServer) {
            return;
        }

        if(context.world().getBlock(context.blockPosition().x, context.blockPosition().y, context.blockPosition().z) == Blocks.PUMPKIN) {
            context.world().setBlock(context.blockPosition(), Blocks.CARVED_PUMPKIN);
        }
    }
}
