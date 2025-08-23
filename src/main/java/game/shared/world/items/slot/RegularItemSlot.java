package game.shared.world.items.slot;

import game.shared.world.items.Item;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;

public class RegularItemSlot implements ItemSlot {
    protected ItemStack representingItemStack;

    public RegularItemSlot() {
        this.representingItemStack = new ItemStack(Items.AIR);
        this.representingItemStack.amount = 0;
    }

    @Override
    public void transferTo(ItemSlot destination, int amount) {
        destination.receiveFrom(this, amount);
        this.representingItemStack.decreaseBy(amount);
    }

    @Override
    public void receiveFrom(ItemSlot source, int amount) {
        if(this.representingItemStack.amount == 0 || this.representingItemStack.getItem() == Items.AIR) {
            this.representingItemStack.setItem(source.getItem());
            this.representingItemStack.setAmount(0);
        }

        int remainder = this.representingItemStack.increaseBy(amount);
        if(remainder > 0) {
            source.receiveFrom(this, remainder);
        }
    }

    @Override
    public Item getItem() {
        return this.representingItemStack.getItem();
    }

    @Override
    public int getAmount() {
        return this.representingItemStack.amount;
    }

    @Override
    public void setItem(Item item) {
        this.representingItemStack.setItem(item);
    }

    @Override
    public void setAmount(int amount) {
        this.representingItemStack.setAmount(amount);
    }

    @Override
    public boolean isItemIdentical(ItemSlot source) {
        return this.representingItemStack.getItem() == source.getItem();
    }

    @Override
    public boolean isEmpty() {
        return this.representingItemStack.getItem() == Items.AIR || this.representingItemStack.amount == 0;
    }

    @Override
    public void swapWith(ItemSlot representingItemSlot){
        if(representingItemSlot.getItem() == Items.AIR) return;

        Item thisSlotsItem = this.getItem();
        int thisSlotsAmount = this.getAmount();

        this.setItem(representingItemSlot.getItem());
        this.setAmount(representingItemSlot.getAmount());

        representingItemSlot.setItem(thisSlotsItem);
        representingItemSlot.setAmount(thisSlotsAmount);
    }

    public ItemStack getItemStack() {
        return this.representingItemStack;
    }

    public void setItemStack(ItemStack stack) {
        this.representingItemStack = stack;
    }
}
