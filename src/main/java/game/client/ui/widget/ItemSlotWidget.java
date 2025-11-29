package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import game.client.ui.item.ItemTextures;
import engine.renderer.Texture;
import game.shared.world.blocks.Blocks;
import game.shared.world.items.BlockItem;
import game.shared.world.items.slot.InfiniteItemSlot;
import game.shared.world.items.slot.ItemSlot;
import org.joml.Vector2f;

public class ItemSlotWidget extends Widget {
    public static Texture SLOT_UNSELECTED_TEXTURE = new Texture("textures/ui/hotbar_unselected.png");
    public static Texture SLOT_SELECTED_TEXTURE = new Texture("textures/ui/hotbar_selected.png");

    public ItemSlot representingItemSlot;
    public ItemSlot holdingSlot;

    public ItemSlotWidget(ItemSlot itemSlot, ItemSlot holdingSlot) {
        this.size = new Vector2f(50, 50);
        this.representingItemSlot = itemSlot;
        this.holdingSlot = holdingSlot;
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        boolean isMouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
        if(isMouseHoveringOver) {
            if(KeyboardAndMouseInput.hasLeftClicked()) {
                if((this.representingItemSlot.isItemIdentical(this.holdingSlot) && this.representingItemSlot instanceof InfiniteItemSlot && !this.representingItemSlot.isEmpty()) || (!this.representingItemSlot.isEmpty() && this.holdingSlot.isEmpty())) {
                    this.representingItemSlot.transferTo(this.holdingSlot, Math.min(this.representingItemSlot.getAmount(), 64 - this.holdingSlot.getAmount()));
                } else if(!this.holdingSlot.isEmpty() && (this.representingItemSlot.isItemIdentical(this.holdingSlot) || this.representingItemSlot.isEmpty())) {
                    this.holdingSlot.transferTo(this.representingItemSlot, Math.min(this.holdingSlot.getAmount(), 64 - this.representingItemSlot.getAmount()));
                } else {
                    this.holdingSlot.swapWith(this.representingItemSlot);
                }
            } else if(KeyboardAndMouseInput.hasRightClicked()) {
                if(!this.holdingSlot.isEmpty() && (this.holdingSlot.isItemIdentical(this.representingItemSlot) || this.representingItemSlot.isEmpty())) {
                    this.holdingSlot.transferTo(this.representingItemSlot, 1);
                }
            }
            this.uiRenderer.renderTexture(SLOT_SELECTED_TEXTURE, this.position, this.size);
        } else {
            this.uiRenderer.renderTexture(SLOT_UNSELECTED_TEXTURE, this.position, this.size);
        }

        if(!this.representingItemSlot.isEmpty()) {
            if(this.representingItemSlot.getItem() instanceof BlockItem blockItem && blockItem.getBlock() != Blocks.AIR) {
                this.gameRenderer.uiRenderer.renderTexture(this.gameRenderer.getBlockItemTexture(blockItem), new Vector2f(this.position.x + this.size.x * 0.1F, this.position.y + this.size.y * 0.1F), new Vector2f(this.size.x * 0.8F,  this.size.y * 0.8F));
            } else {
                this.gameRenderer.uiRenderer.renderTexture(ItemTextures.getTexture(this.representingItemSlot.getItem().id), new Vector2f(this.position.x + this.size.x * 0.1F, this.position.y + this.size.y * 0.1F), new Vector2f(this.size.x * 0.8F,  this.size.y * 0.8F));
            }
            if(this.representingItemSlot.getAmount() != 1) {
                this.gameRenderer.textRenderer.renderTextWithShadow(String.valueOf(this.representingItemSlot.getAmount()),this.position.x + this.size.x - 5 - this.gameRenderer.textRenderer.getWidth(String.valueOf(this.representingItemSlot.getAmount())), this.position.y);
            }
        }

        if(isMouseHoveringOver && !this.representingItemSlot.isEmpty() && this.holdingSlot.isEmpty()) {
            this.uiRenderer.addTooltipToBeRendered(new Tooltip(this.representingItemSlot.getItem().getName(), new Vector2f(mouseX + 8, mouseY - 24 - 8)));
        }
    }
}
