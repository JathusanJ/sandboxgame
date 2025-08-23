package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.shared.recipes.Recipe;
import game.shared.recipes.CraftingRecipes;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import game.shared.world.items.slot.InventoryItemSlot;
import game.shared.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class CraftingTableScreen extends ContainerScreen implements CraftingScreen {
    public ArrayList<ItemSlotWidget> inputSlots = new ArrayList<>();
    public ArrayList<ItemSlotWidget> playerInventorySlots = new ArrayList<>();
    public ItemSlotWidget outputSlot = new ItemSlotWidget(new CraftingTableOutputSlot(this), holdingSlot);

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
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.crafting_table"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);
        this.uiRenderer.renderTexture(craftingArrow, new Vector2f(this.getScreenWidth() / 2F + 2.5F * 50 - 100, this.getScreenHeight() / 2F + 1.5F * 50 - 10), new Vector2f(64, 64));

        // Find recipe
        ArrayList<Item> input = new ArrayList<>();
        for(ItemSlotWidget slot : this.inputSlots) {
            input.add(slot.representingItemSlot.getItem());
        }

        Recipe recipe = CraftingRecipes.findRecipe(input, 3, 3);
        if(recipe != null) {
            this.outputSlot.representingItemSlot.setItem(recipe.recipeOutput);
            this.outputSlot.representingItemSlot.setAmount(recipe.amount);
        } else {
            this.outputSlot.representingItemSlot.setItem(Items.AIR);
        }
    }

    @Override
    public void renderBeforeWidgets(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderColoredQuad(new Vector2f(this.getScreenWidth() / 2F - 4.75F * 50, this.getScreenHeight() / 2F - 5.25F * 50), new Vector2f(475, 475), new Vector4f(0.25F, 0.25F, 0.25F, 0.95F));
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(null);
        if(!this.holdingSlot.isEmpty()) {
            this.gameRenderer.player.putInInventory(this.holdingSlot.getItemStack());
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
            this.playerInventorySlots.get(x).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 5 * 50);
        }
    }

    @Override
    public void onItemTaken(int amount) {
        if(amount < 1) return;
        for (int i = 0; i < 9; i++) {
            this.inputSlots.get(i).representingItemSlot.setAmount(this.inputSlots.get(i).representingItemSlot.getAmount() - amount);
            if(this.inputSlots.get(i).representingItemSlot.getAmount() == 0) {
                this.inputSlots.get(i).representingItemSlot.setItem(Items.AIR);
            }
        }
    }
}
