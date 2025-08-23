package game.shared.world.items.slot;

import game.shared.world.blocks.block_entity.BlockEntity;
import game.shared.world.items.Item;
import game.shared.world.items.ItemStack;

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
