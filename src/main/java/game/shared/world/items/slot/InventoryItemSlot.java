package game.shared.world.items.slot;

import game.shared.world.items.Item;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;

public class InventoryItemSlot implements ItemSlot {
    int index;
    ItemStack[] inventory;

    public InventoryItemSlot(ItemStack[] inventory, int index) {
        this.inventory = inventory;
        this.index = index;
    }

    @Override
    public void transferTo(ItemSlot destination, int amount) {
        destination.receiveFrom(this, amount);
        this.inventory[this.index].decreaseBy(amount);
    }

    @Override
    public void receiveFrom(ItemSlot source, int amount) {
        if(this.inventory[this.index].getItem() == Items.AIR) {
            this.inventory[this.index].setItem(source.getItem());
            this.inventory[this.index].setAmount(0);
        }
        int remainder = this.inventory[this.index].increaseBy(amount);
        if(remainder > 0) {
            source.receiveFrom(this, remainder);
        }
    }

    @Override
    public Item getItem() {
        return this.inventory[this.index].getItem();
    }

    @Override
    public int getAmount() {
        return this.inventory[this.index] == null ? 0 : this.inventory[this.index].amount;
    }

    @Override
    public void setItem(Item item) {
        if(this.inventory[this.index] == null) {
            this.inventory[this.index] = new ItemStack(item);
        } else {
            this.inventory[this.index].setItem(item);
        }
    }

    @Override
    public void setAmount(int amount) {
        if(this.inventory[this.index] == null) {
            this.inventory[this.index] = new ItemStack(Items.AIR);
        }
        this.inventory[this.index].setAmount(amount);
    }

    @Override
    public boolean isItemIdentical(ItemSlot source) {
        return this.inventory[this.index] != null && this.inventory[this.index].getItem() == source.getItem();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory[this.index] == null || this.inventory[this.index].getItem() == Items.AIR;
    }
}
