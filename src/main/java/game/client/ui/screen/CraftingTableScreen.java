package game.client.ui.screen;

import game.client.ui.text.Font;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.logic.recipes.Recipe;
import game.logic.recipes.CraftingRecipes;
import game.logic.world.items.BlockItem;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.slot.InventoryItemSlot;
import game.logic.world.items.slot.ItemSlot;
import game.logic.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;

import java.util.ArrayList;

public class CraftingTableScreen extends Screen {
    public RegularItemSlot holdingSlot = new RegularItemSlot();
    public ArrayList<ItemSlotWidget> inputSlots = new ArrayList<>();
    public ArrayList<ItemSlotWidget> playerInventorySlots = new ArrayList<>();
    public ItemSlotWidget outputSlot = new ItemSlotWidget(new CraftingTableOutputSlot(this), holdingSlot);
    public Recipe currentRecipe;

    public CraftingTableScreen() {
        for (int i = 0; i < 9; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new RegularItemSlot(), holdingSlot);
            this.inputSlots.add(itemSlotWidget);
        }

        for (int i = 0; i < this.gameRenderer.player.inventory.length; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new InventoryItemSlot(this.gameRenderer.player.inventory, i), holdingSlot);
            this.playerInventorySlots.add(itemSlotWidget);
        }

        this.renderableWidgets.addAll(this.inputSlots);
        this.renderableWidgets.addAll(this.playerInventorySlots);
        this.renderableWidgets.add(this.outputSlot);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        if(!this.holdingSlot.isEmpty()) {
            if(this.holdingSlot.getItem() instanceof BlockItem blockItem) {
                this.gameRenderer.renderBlock(blockItem.getBlock(), new Vector2f(mouseX, mouseY), new Vector2f(40, 40));
            }
            if(this.holdingSlot.getAmount() != 1) {
                this.uiRenderer.renderText(String.valueOf(this.holdingSlot.getAmount()), new Vector2f(mouseX + 40 - Font.getTextWidth(String.valueOf(this.holdingSlot.getAmount()), 24), mouseY), 24);
            }
        }

        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.crafting_table"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);

        // Find recipe
        ArrayList<Item> input = new ArrayList<>();
        for(ItemSlotWidget slot : this.inputSlots) {
            input.add(slot.representingItemSlot.getItem());
        }

        Recipe recipe = CraftingRecipes.findRecipe(input, 3, 3);
        if(recipe != null) {
            this.outputSlot.representingItemSlot.setItem(recipe.recipeOutput);
            this.outputSlot.representingItemSlot.setAmount(1);
        } else {
            this.outputSlot.representingItemSlot.setItem(Items.AIR);
        }
    }

    // Duplicated from the block and item selector screen, I should look into reducing duplicated code sometime
    @Override
    public void close() {
        this.gameRenderer.setScreen(null);
        if(!this.holdingSlot.isEmpty()) {
            // Find the nearest slot with the same item
            for (int i = 0; i < this.gameRenderer.player.inventory.length; i++) {
                ItemStack itemstack = this.gameRenderer.player.inventory[i];
                if(this.holdingSlot.getItem() == itemstack.getItem() && itemstack.amount < 64) {
                    int amountToTransfer = Math.min(this.holdingSlot.getAmount(), 64 - itemstack.amount);
                    itemstack.setAmount(itemstack.amount + amountToTransfer);
                    this.holdingSlot.setAmount(this.holdingSlot.getAmount() - amountToTransfer);
                }
                if(this.holdingSlot.getAmount() <= 0) break;
            }
            if(!this.holdingSlot.isEmpty()) {
                // Find the nearest empty slot
                for (int i = 0; i < this.gameRenderer.player.inventory.length; i++) {
                    ItemStack itemstack = this.gameRenderer.player.inventory[i];
                    if(itemstack.getItem() == Items.AIR) {
                        itemstack.setItem(this.holdingSlot.getItem());
                        itemstack.setAmount(0);
                        int amountToTransfer = Math.min(this.holdingSlot.getAmount(), 64 - itemstack.amount);
                        itemstack.setAmount(itemstack.amount + amountToTransfer);
                        this.holdingSlot.setAmount(this.holdingSlot.getAmount() - amountToTransfer);
                    }
                    if(this.holdingSlot.getAmount() <= 0) break;
                }
                if(!this.holdingSlot.isEmpty()) {
                    // Drop the item into the world (when entities are added)
                }
            }
        }
    }

    @Override
    public void positionContent() {
        // Input slots
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                this.inputSlots.get(y * 3 + x).position = new Vector2f(this.getScreenWidth() / 2F - 3.5F * 50 + x * 50, this.getScreenHeight() / 2F + 2.5F * 50 - y * 50);
            }
        }

        this.outputSlot.position = new Vector2f(this.getScreenWidth() / 2F + 2.5F * 50, this.getScreenHeight() / 2F + 1.5F * 50);

        // Inventory (rows 2 - 4)
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.playerInventorySlots.get(y * 9 + x + 9).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 1.5F * 50 - y * 50);
            }
        }

        // Hotbar (row 1)
        for(int x = 0; x < 9; x++) {
            this.playerInventorySlots.get(x).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 1.5F * 50 - 3 * 50);
        }
    }

    public void onItemTaken(int amount) {
        if(amount < 1) return;
        for (int i = 0; i < 9; i++) {
            this.inputSlots.get(i).representingItemSlot.setAmount(this.inputSlots.get(i).representingItemSlot.getAmount() - amount);
            if(this.inputSlots.get(i).representingItemSlot.getAmount() == 0) {
                this.inputSlots.get(i).representingItemSlot.setItem(Items.AIR);
            }
        }
    }

    public static class CraftingTableOutputSlot extends RegularItemSlot {
        public CraftingTableScreen screen;

        public CraftingTableOutputSlot(CraftingTableScreen craftingTableScreen) {
            this.screen = craftingTableScreen;
            this.representingItemStack = new ItemStack(Items.AIR);
            this.representingItemStack.amount = 0;
        }

        @Override
        public void transferTo(ItemSlot destination, int amount) {
            destination.receiveFrom(this, amount);
            this.screen.onItemTaken(amount);
        }

        @Override
        public void receiveFrom(ItemSlot source, int amount) {
            // Send back the amount transferred to here
            source.receiveFrom(this, amount);
            if(source.isItemIdentical(this) && source.getAmount() <= 64 - this.representingItemStack.amount) {
                // And give more if the item in the holding slot is identical
                source.receiveFrom(this, this.representingItemStack.amount);
                this.screen.onItemTaken(this.representingItemStack.amount);
            }
        }
    }
}
