package game.client.ui.screen;

import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.slot.ItemSlot;
import game.logic.world.items.slot.RegularItemSlot;

public interface CraftingScreen {
    void onItemTaken(int amount);

    class CraftingTableOutputSlot extends RegularItemSlot {
        public CraftingScreen screen;

        public CraftingTableOutputSlot(CraftingScreen craftingScreen) {
            this.screen = craftingScreen;
            this.representingItemStack = new ItemStack(Items.AIR);
            this.representingItemStack.amount = 0;
        }

        @Override
        public void transferTo(ItemSlot destination, int amount) {
            destination.receiveFrom(this, amount);
            this.screen.onItemTaken(1);
        }

        @Override
        public void receiveFrom(ItemSlot source, int amount) {
            // Send back the amount transferred to here
            source.receiveFrom(this, amount);
            if(source.isItemIdentical(this) && source.getAmount() <= 64 - this.representingItemStack.amount) {
                // And give more if the item in the holding slot is identical
                source.receiveFrom(this, this.representingItemStack.amount);
                this.screen.onItemTaken(1);
            }
        }
    }
}