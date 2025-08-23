package game.client.ui.screen;

import game.client.ui.widget.ItemSlotWidget;
import game.shared.world.items.Item;
import game.shared.world.items.Items;
import game.shared.world.items.slot.InfiniteItemSlot;
import game.shared.world.items.slot.InventoryItemSlot;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class BlockAndItemSelectorScreen extends ContainerScreen {
    public ArrayList<ItemSlotWidget> itemSlots = new ArrayList<>();
    public BlockAndItemSelectorScreen() {
        List<Item> items = Items.sorted();

        int counter = 0;
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 9; x++) {
                ItemSlotWidget slotWidget;
                if(counter < items.size()) {
                    slotWidget = new ItemSlotWidget(new InfiniteItemSlot(items.get(counter)), this.holdingSlot);
                } else {
                    slotWidget = new ItemSlotWidget(new InfiniteItemSlot(Items.AIR), this.holdingSlot);
                }
                itemSlots.add(slotWidget);
                this.renderableWidgets.add(slotWidget);
                counter++;
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
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 9; x++) {
                itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F + 2 * 50 - y * 50);
                counter++;
            }
        }

        for(int x = 0; x < 9; x++) {
            itemSlots.get(counter).position = new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50 + x * 50, this.getScreenHeight() / 2F - 3 * 50);;
            counter++;
        }
    }
}
