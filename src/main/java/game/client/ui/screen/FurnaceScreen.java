package game.client.ui.screen;

import engine.renderer.Texture;
import game.client.ui.item.ItemTextures;
import game.client.ui.text.Font;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.logic.world.blocks.block_entity.FurnaceBlockEntity;
import game.logic.world.items.BlockItem;
import game.logic.world.items.slot.InventoryItemSlot;
import game.logic.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;

import java.util.ArrayList;

public class FurnaceScreen extends ContainerScreen {
    public FurnaceBlockEntity blockEntity;
    public ArrayList<ItemSlotWidget> playerInventorySlots = new ArrayList<>();
    public ItemSlotWidget inputSlot;
    public ItemSlotWidget fuelSlot;
    public ItemSlotWidget outputSlot;
    public Texture test = new Texture("textures/texture.png");

    public FurnaceScreen(FurnaceBlockEntity blockEntity) {
        this.blockEntity = blockEntity;

        this.inputSlot = new ItemSlotWidget(blockEntity.inputSlot, holdingSlot);
        this.fuelSlot = new ItemSlotWidget(blockEntity.fuelSlot, holdingSlot);
        this.outputSlot = new ItemSlotWidget(blockEntity.outputSlot, holdingSlot);

        for (int i = 0; i < this.gameRenderer.player.inventory.length; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new InventoryItemSlot(this.gameRenderer.player.inventory, i), holdingSlot);
            this.playerInventorySlots.add(itemSlotWidget);
        }

        this.renderableWidgets.addAll(this.playerInventorySlots);
        this.renderableWidgets.add(this.inputSlot);
        this.renderableWidgets.add(this.fuelSlot);
        this.renderableWidgets.add(this.outputSlot);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.furnace"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);

        this.uiRenderer.renderTexture(test, new Vector2f(this.getScreenWidth() / 2F - 125, this.getScreenHeight() / 2F + 1.5F * 50), new Vector2f((float) (250 * this.blockEntity.burnTime) / 100, 50));
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

        this.inputSlot.position = new Vector2f(this.getScreenWidth() / 2F - 2.5F * 50, this.getScreenHeight() / 2F + 2.5F * 50);
        this.fuelSlot.position = new Vector2f(this.getScreenWidth() / 2F - 2.5F * 50, this.getScreenHeight() / 2F + 0.5F * 50);
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
}
