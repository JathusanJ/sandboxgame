package game.client.ui.screen;

import game.client.ui.item.ItemTextures;
import game.shared.world.items.BlockItem;
import game.shared.world.items.slot.RegularItemSlot;
import org.joml.Vector2f;

public abstract class ContainerScreen extends Screen {
    public RegularItemSlot holdingSlot = new RegularItemSlot();

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        super.render(deltaTime, mouseX, mouseY);

        if(!this.holdingSlot.isEmpty()) {
            if(this.holdingSlot.getItem() instanceof BlockItem blockItem) {
                this.gameRenderer.uiRenderer.renderTexture(this.gameRenderer.getBlockItemTexture(blockItem), new Vector2f(mouseX, mouseY), new Vector2f(40, 40));
            } else {
                this.gameRenderer.uiRenderer.renderTexture(ItemTextures.getTexture(this.holdingSlot.getItem().id), new Vector2f(mouseX, mouseY), new Vector2f(40, 40));
            }
            if(this.holdingSlot.getAmount() != 1) {
                this.gameRenderer.textRenderer.renderText(String.valueOf(this.holdingSlot.getAmount()), 24, mouseX + 40 - this.gameRenderer.textRenderer.getWidth(String.valueOf(this.holdingSlot.getAmount()), 24), mouseY);
            }
        }
    }
}
