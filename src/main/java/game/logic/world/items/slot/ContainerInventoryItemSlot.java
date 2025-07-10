package game.logic.world.items.slot;

import game.logic.world.blocks.block_entity.BlockEntity;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;

public class ContainerInventoryItemSlot extends InventoryItemSlot {
    BlockEntity blockEntity;

    public ContainerInventoryItemSlot(ItemStack[] inventory, int index, BlockEntity containerBlockEntity) {
        super(inventory, index);
        this.blockEntity = containerBlockEntity;
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
