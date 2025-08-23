package game.shared.world.items.slot;

import game.shared.world.blocks.block_entity.BlockEntity;
import game.shared.world.items.Item;

public class ContainerItemSlot extends RegularItemSlot {
    public BlockEntity blockEntity;

    public ContainerItemSlot(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void transferTo(ItemSlot destination, int amount) {
        super.transferTo(destination, amount);
        this.blockEntity.needsSaving();
    }

    @Override
    public void receiveFrom(ItemSlot source, int amount) {
        super.receiveFrom(source, amount);
        this.blockEntity.needsSaving();
    }

    @Override
    public void setItem(Item item) {
        super.setItem(item);
        this.blockEntity.needsSaving();
    }

    @Override
    public void setAmount(int amount) {
        super.setAmount(amount);
        this.blockEntity.needsSaving();
    }
}
