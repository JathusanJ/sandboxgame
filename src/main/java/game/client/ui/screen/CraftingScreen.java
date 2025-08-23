package game.client.ui.screen;

import engine.renderer.Texture;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.shared.world.items.slot.ItemSlot;
import game.shared.world.items.slot.RegularItemSlot;

public interface CraftingScreen {
    Texture craftingArrow = new Texture("textures/ui/crafting_arrow.png");
    Texture craftingArrowOutline = new Texture("textures/ui/crafting_arrow_outline.png");
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