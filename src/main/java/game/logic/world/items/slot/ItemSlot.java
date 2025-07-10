package game.logic.world.items.slot;

import game.logic.world.items.Item;

public interface ItemSlot {
    void transferTo(ItemSlot destination, int amount);
    void receiveFrom(ItemSlot source, int amount);

    Item getItem();
    int getAmount();
    void setItem(Item item);
    void setAmount(int amount);

    boolean isItemIdentical(ItemSlot source);
    boolean isEmpty();

    default void swapWith(ItemSlot representingItemSlot){};
}
