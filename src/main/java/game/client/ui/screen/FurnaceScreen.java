package game.client.ui.screen;

import engine.renderer.Texture;
import game.client.ui.text.Language;
import game.client.ui.widget.ItemSlotWidget;
import game.shared.world.blocks.block_entity.FurnaceBlockEntity;
import game.shared.world.items.slot.InventoryItemSlot;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class FurnaceScreen extends ContainerScreen {
    public FurnaceBlockEntity blockEntity;
    public ArrayList<ItemSlotWidget> playerInventorySlots = new ArrayList<>();
    public ItemSlotWidget inputSlot;
    public ItemSlotWidget fuelSlot;
    public ItemSlotWidget outputSlot;
    public static Texture fireTexture = new Texture("textures/ui/fire.png");
    public static Texture fireOutlineTexture = new Texture("textures/ui/fire_outline.png");

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
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.furnace"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F + 3.5F * 50), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.inventory"), new Vector2f(this.getScreenWidth() / 2F - 4.5F * 50, this.getScreenHeight() / 2F - 0.5F * 50), 24);

        this.uiRenderer.renderTexture(CraftingScreen.craftingArrowOutline, new Vector2f(this.getScreenWidth() / 2F - 32, this.getScreenHeight() / 2F + 1.5F * 50 - 16), new Vector2f(64,64));
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) (this.getScreenWidth() / 2F - 32), (int) (this.getScreenHeight() / 2F + 1.5F * 50 - 16), (int) (this.blockEntity.burnTime / 100F * 64),64);
        this.uiRenderer.renderTexture(CraftingScreen.craftingArrow, new Vector2f(this.getScreenWidth() / 2F - 32, this.getScreenHeight() / 2F + 1.5F * 50 - 16), new Vector2f(64,64));
        glScissor(0,0, this.getScreenWidth(), this.getScreenHeight());
        glDisable(GL_SCISSOR_TEST);

        float maxFuelTime = 100;
        if(this.blockEntity.lastFuelItem != null) {
            maxFuelTime = FurnaceBlockEntity.itemFuelTimes.get(this.blockEntity.lastFuelItem);
        }

        this.uiRenderer.renderTexture(fireOutlineTexture, new Vector2f(this.getScreenWidth() / 2F - 125, this.getScreenHeight() / 2F + 1.5F * 50), new Vector2f(50, 50));
        glEnable(GL_SCISSOR_TEST);
        glScissor((int) (this.getScreenWidth() / 2F - 125), (int) (this.getScreenHeight() / 2F + 1.5F * 50), 50, (int) Math.min(this.blockEntity.remainingFuelTime / maxFuelTime * 50, 50));
        this.uiRenderer.renderTexture(fireTexture, new Vector2f(this.getScreenWidth() / 2F - 125, this.getScreenHeight() / 2F + 1.5F * 50), new Vector2f(50, 50));
        glScissor(0,0, this.getScreenWidth(), this.getScreenHeight());
        glDisable(GL_SCISSOR_TEST);
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

        this.inputSlot.position = new Vector2f(this.getScreenWidth() / 2F - 2.5F * 50, this.getScreenHeight() / 2F + 2.5F * 50);
        this.fuelSlot.position = new Vector2f(this.getScreenWidth() / 2F - 2.5F * 50, this.getScreenHeight() / 2F + 0.5F * 50);
        this.outputSlot.position = new Vector2f(this.getScreenWidth() / 2F + 1.5F * 50, this.getScreenHeight() / 2F + 1.5F * 50);

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
