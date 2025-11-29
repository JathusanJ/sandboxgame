package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.NineSliceTexture;
import engine.renderer.Texture;
import game.client.ui.screen.WorldSelectScreen;
import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import game.shared.world.World;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class WorldWidget extends Widget {
    private SingleplayerWorld world;
    private WorldSelectScreen worldSelectScreen;

    public static Texture BORDER_SELECTED_TEXTURE = new NineSliceTexture("textures/ui/border_selected.png", 1 / 25F, 1 / 25F, 0.1F, 0.1F);

    public WorldWidget(SingleplayerWorld world, WorldSelectScreen worldSelectScreen) {
        this.world = world;
        this.worldSelectScreen = worldSelectScreen;
        this.size = new Vector2f(400F, 75F);
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        if(this.world.worldFolder == null) {
            this.gameRenderer.textRenderer.renderTextWithShadow(this.world.worldFolderName, this.position.x + 10F, this.position.y + this.size.y - 37.5F, new Vector4f(1F, 0.1F, 0.1F, 1F));
            this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.world_select.invalid"), this.position.x + 30F, this.position.y + this.size.y - 37.5F - 25F, new Vector4f(0.33F, 0.33F, 0.33F, 1F));
        } else {
            String lowerText = Language.translate("ui.world_select.last_saved_in").formatted(this.world.lastSavedIn);

            if(this.world.commandsEnabled) {
                lowerText = lowerText + " | " + Language.translate("ui.world_select.commands_enabled");
            }

            if(this.world.chunkVersion > World.CHUNK_VERSION) {
                lowerText = Language.translate("ui.world_select.format_too_new") + " | " + lowerText;
                this.gameRenderer.textRenderer.renderTextWithShadow(this.world.name == null ? this.world.worldFolderName : this.world.name, this.position.x + 10F, this.position.y + this.size.y - 37.5F, new Vector4f(1F, 0.1F, 0.1F, 1F));
                this.gameRenderer.textRenderer.renderTextWithShadow(lowerText, this.position.x + 30F, this.position.y + this.size.y - 37.5F - 25F, new Vector4f(0.33F, 0.33F, 0.33F, 1F));
            } else {
                this.gameRenderer.textRenderer.renderTextWithShadow(this.world.name == null ? this.world.worldFolderName : this.world.name, this.position.x + 10F, this.position.y + this.size.y - 37.5F);
                this.gameRenderer.textRenderer.renderTextWithShadow(lowerText, this.position.x + 30F, this.position.y + this.size.y - 37.5F - 25F, new Vector4f(0.8F, 0.8F, 0.8F, 1F));

                boolean mouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
                if (mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked()) {
                    this.worldSelectScreen.selectWorld(this);
                }

                if (this.worldSelectScreen.getSelectedWorld() == this) {
                    this.uiRenderer.renderTexture(BORDER_SELECTED_TEXTURE, this.position, this.size);
                }
            }
        }
    }

    public SingleplayerWorld getWorld() {
        return this.world;
    }
}
