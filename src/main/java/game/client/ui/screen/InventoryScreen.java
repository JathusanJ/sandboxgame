package game.client.ui.screen;

import game.client.ui.item.ItemTextures;
import game.client.ui.text.Font;
import game.client.ui.widget.ItemSlotWidget;
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

public class InventoryScreen extends Screen {
    public ArrayList<ItemSlotWidget> itemSlots = new ArrayList<>();

    public RegularItemSlot holdingSlot;

    public InventoryScreen() {
        this.holdingSlot = new RegularItemSlot();

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
        int counter = 0;
        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - (y + 2) * 50);
                counter++;
            }
        }

        for(int x = 0; x < 9; x++) {
            itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 5 * 50);;
            counter++;
        }
    }
}
