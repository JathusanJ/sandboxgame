package game.client.ui.screen;

import game.client.ui.item.ItemTextures;
import game.client.ui.text.Font;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.logic.world.blocks.block_entity.BarrelBlockEntity;
import game.logic.world.items.BlockItem;
import game.logic.world.items.slot.ContainerInventoryItemSlot;
import game.logic.world.items.slot.InventoryItemSlot;
import game.logic.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;

import java.util.ArrayList;

public class BarrelInventoryScreen extends Screen {
    public RegularItemSlot holdingSlot = new RegularItemSlot();
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

        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.barrel"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);
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
