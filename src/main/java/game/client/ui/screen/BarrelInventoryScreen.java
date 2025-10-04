package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.shared.world.blocks.block_entity.BarrelBlockEntity;
import game.shared.world.items.slot.ContainerInventoryItemSlot;
import game.shared.world.items.slot.InventoryItemSlot;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class BarrelInventoryScreen extends ContainerScreen {
    public BarrelBlockEntity barrelBlockEntity;

    public ArrayList<ItemSlotWidget> barrelSlots = new ArrayList<>();
    public ArrayList<ItemSlotWidget> playerInventorySlots = new ArrayList<>();

    public BarrelInventoryScreen(BarrelBlockEntity barrelBlockEntity) {
        this.barrelBlockEntity = barrelBlockEntity;

        for (int i = 0; i < 27; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new ContainerInventoryItemSlot(this.barrelBlockEntity.contents, i, barrelBlockEntity), holdingSlot);
            this.barrelSlots.add(itemSlotWidget);
        }

        for (int i = 0; i < this.gameRenderer.player.inventory.length; i++) {
            ItemSlotWidget itemSlotWidget = new ItemSlotWidget(new InventoryItemSlot(this.gameRenderer.player.inventory, i), holdingSlot);
            this.playerInventorySlots.add(itemSlotWidget);
        }

        this.renderableWidgets.addAll(this.barrelSlots);
        this.renderableWidgets.addAll(this.playerInventorySlots);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.barrel"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);
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
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.barrelSlots.get(y * 9 + x).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F + 2.5F * 50 - y * 50);
            }
        }

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
