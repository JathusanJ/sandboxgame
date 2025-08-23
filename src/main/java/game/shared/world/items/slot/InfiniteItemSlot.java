package game.shared.world.items.slot;

import game.shared.world.items.Item;
import game.shared.world.items.Items;

public class InfiniteItemSlot implements ItemSlot {
    public Item sourceItem;

    public InfiniteItemSlot(Item sourceItem) {
        this.sourceItem = sourceItem;
    }

    @Override
    public void transferTo(ItemSlot destination, int amount) {
        destination.receiveFrom(this, amount);
    }

    @Override
    public void receiveFrom(ItemSlot source, int amount) {

    }

    @Override
    public Item getItem() {
        return this.sourceItem;
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    public void setItem(Item item) {}

    @Override
    public void setAmount(int amount) {}

    @Override
    public boolean isItemIdentical(ItemSlot source) {
        return source.getAmount() == 0 || this.sourceItem == source.getItem();
    }

    @Override
    public boolean isEmpty() {
        return this.sourceItem == Items.AIR;
    }
}
