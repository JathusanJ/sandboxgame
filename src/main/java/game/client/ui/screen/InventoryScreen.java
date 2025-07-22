package game.client.ui.screen;

import game.client.ui.item.ItemTextures;
import game.client.ui.text.Font;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.logic.recipes.CraftingRecipes;
import game.logic.recipes.Recipe;
import game.logic.world.creature.ItemCreature;
import game.logic.world.items.BlockItem;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.slot.InfiniteItemSlot;
import game.logic.world.items.slot.InventoryItemSlot;
import game.logic.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class InventoryScreen extends Screen implements CraftingScreen {
    public ArrayList<ItemSlotWidget> itemSlots = new ArrayList<>();
    public RegularItemSlot holdingSlot = new RegularItemSlot();
    public ItemSlotWidget craftingOutputSlot = new ItemSlotWidget(new CraftingScreen.CraftingTableOutputSlot(this), holdingSlot);
    public ArrayList<ItemSlotWidget> craftingInputSlots = new ArrayList<>();

    public InventoryScreen() {
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                ItemSlotWidget slotWidget = new ItemSlotWidget(new InventoryItemSlot(this.gameRenderer.player.inventory, 9 + y * 9 + x), this.holdingSlot);
                itemSlots.add(slotWidget);
                this.renderableWidgets.add(slotWidget);
            }
        }

        for(int x = 0; x < 9; x++) {
            ItemSlotWidget slotWidget = new ItemSlotWidget(new InventoryItemSlot(this.gameRenderer.player.inventory, x), this.holdingSlot);
            itemSlots.add(slotWidget);
            this.renderableWidgets.add(slotWidget);
        }

        for (int i = 0; i < 4; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new RegularItemSlot(), holdingSlot);
            this.craftingInputSlots.add(itemSlotWidget);
            this.renderableWidgets.add(itemSlotWidget);
        }

        this.renderableWidgets.add(this.craftingOutputSlot);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        if(!this.holdingSlot.isEmpty()) {
            if(this.holdingSlot.getItem() instanceof BlockItem blockItem) {
                this.gameRenderer.uiRenderer.renderTexture(this.gameRenderer.getBlockItemTexture(blockItem.getBlock()), new Vector2f(mouseX, mouseY), new Vector2f(40, 40));
            } else {
                this.gameRenderer.uiRenderer.renderTexture(ItemTextures.getTexture(this.holdingSlot.getItem().id), new Vector2f(mouseX, mouseY), new Vector2f(40, 40));
            }
            if(this.holdingSlot.getAmount() != 1) {
                this.uiRenderer.renderText(String.valueOf(this.holdingSlot.getAmount()), new Vector2f(mouseX + 40 - Font.getTextWidth(String.valueOf(this.holdingSlot.getAmount()), 24), mouseY), 24);
            }
        }

        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);

        ArrayList<Item> input = new ArrayList<>();
        for(ItemSlotWidget slot : this.craftingInputSlots) {
            input.add(slot.representingItemSlot.getItem());
        }

        Recipe recipe = CraftingRecipes.findRecipe(input, 2, 2);
        if(recipe != null) {
            this.craftingOutputSlot.representingItemSlot.setItem(recipe.recipeOutput);
            this.craftingOutputSlot.representingItemSlot.setAmount(1);
        } else {
            this.craftingOutputSlot.representingItemSlot.setItem(Items.AIR);
        }
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(null);
        if(!this.holdingSlot.isEmpty()) {
            this.gameRenderer.player.putInInventory(this.holdingSlot.getItemStack());
        }

        for(ItemSlotWidget itemSlotWidget : this.craftingInputSlots) {
            if(!itemSlotWidget.representingItemSlot.isEmpty()) {
                this.gameRenderer.player.putInInventory(((RegularItemSlot) itemSlotWidget.representingItemSlot).getItemStack());
            }
        }
    }

    @Override
    public void positionContent() {
        int counter = 0;
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - (y + 1) * 50 - 24);
                counter++;
            }
        }

        for(int x = 0; x < 9; x++) {
            itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 5 * 50);;
            counter++;
        }

        for(int y = 0; y < 2; y++) {
            for(int x = 0; x < 2; x++) {
                this.craftingInputSlots.get(y * 2 + x).position = new Vector2f(this.getScreenWidth() / 2F - 3.5F * 50 + x * 50, this.getScreenHeight() / 2F + 2.5F * 50 - y * 50 - 25);
            }
        }

        this.craftingOutputSlot.position = new Vector2f(this.getScreenWidth() / 2F + 1.5F * 50, this.getScreenHeight() / 2F + 1.5F * 50);
    }

    @Override
    public void onItemTaken(int amount) {
        if(amount < 1) {
            return;
        }

        for(ItemSlotWidget craftingInputSlot : this.craftingInputSlots) {
            craftingInputSlot.representingItemSlot.setAmount(craftingInputSlot.representingItemSlot.getAmount() - amount);
            if (craftingInputSlot.representingItemSlot.getAmount() == 0) {
                craftingInputSlot.representingItemSlot.setItem(Items.AIR);
            }
        }
    }
}
