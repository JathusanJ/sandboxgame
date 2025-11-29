package game.client.ui.screen;

import engine.renderer.Camera;
import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.client.ui.widget.PlayerInUIWidget;
import game.shared.recipes.CraftingRecipes;
import game.shared.recipes.Recipe;
import game.shared.world.creature.OtherPlayer;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import game.shared.world.items.slot.InventoryItemSlot;
import game.shared.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class InventoryScreen extends ContainerScreen implements CraftingScreen {
    public ArrayList<ItemSlotWidget> itemSlots = new ArrayList<>();
    public ItemSlotWidget craftingOutputSlot = new ItemSlotWidget(new CraftingScreen.CraftingTableOutputSlot(this), holdingSlot);
    public ArrayList<ItemSlotWidget> craftingInputSlots = new ArrayList<>();
    public PlayerInUIWidget playerInUI = new PlayerInUIWidget(new OtherPlayer(), new Camera());
    public float timeSinceOpening = 0;

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
        this.renderableWidgets.add(this.playerInUI);
        ((OtherPlayer) this.playerInUI.player).skin = SandboxGame.getInstance().getPlayerProfile().getSkin();
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.inventory"), this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50);
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.inventory.crafting"), this.getScreenWidth() / 2F - 25, this.getScreenHeight() / 2F + 3F * 50);

        this.uiRenderer.renderTexture(craftingArrow, new Vector2f(this.getScreenWidth() / 2F + 3.5F * 50 - 80, this.getScreenHeight() / 2F + 1.5F * 50 - 7), new Vector2f(64,64));

        ArrayList<Item> input = new ArrayList<>();
        for(ItemSlotWidget slot : this.craftingInputSlots) {
            input.add(slot.representingItemSlot.getItem());
        }

        Recipe recipe = CraftingRecipes.findRecipe(input, 2, 2);
        if(recipe != null) {
            this.craftingOutputSlot.representingItemSlot.setItem(recipe.recipeOutput);
            this.craftingOutputSlot.representingItemSlot.setAmount(recipe.amount);
        } else {
            this.craftingOutputSlot.representingItemSlot.setItem(Items.AIR);
        }
    }

    @Override
    public void renderBeforeWidgets(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderColoredQuad(new Vector2f(this.getScreenWidth() / 2F - 4.75F * 50, this.getScreenHeight() / 2F - 5.25F * 50), new Vector2f(475, 475), new Vector4f(0.25F, 0.25F, 0.25F, 0.95F));

        this.timeSinceOpening = (float) (this.timeSinceOpening + deltaTime);
        this.playerInUI.camera.position.set(this.playerInUI.player.position);
        this.playerInUI.camera.yaw = (float) (Math.sin(this.timeSinceOpening * 0.3) * 20) + 180F;
        this.playerInUI.camera.pitch = (float) (Math.cos(this.timeSinceOpening * 0.4) * 20);
        Vector3f direction = this.playerInUI.camera.getDirection();
        this.playerInUI.camera.position.add(direction.mul(-2.5F)).add(0, 1F, 0);
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
                this.craftingInputSlots.get(y * 2 + x).position = new Vector2f(this.getScreenWidth() / 2F - 25 + x * 50, this.getScreenHeight() / 2F + 2.5F * 50 - y * 50 - 25);
            }
        }

        this.craftingOutputSlot.position = new Vector2f(this.getScreenWidth() / 2F + 3.5F * 50, this.getScreenHeight() / 2F + 1.5F * 50);

        this.playerInUI.size = new Vector2f(300, 300);
        this.playerInUI.position = new Vector2f(this.getScreenWidth() / 2F - 275, this.getScreenHeight() / 2F - 50F);

        this.playerInUI.camera.perspectiveProjection(300, 300, 70F);
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
