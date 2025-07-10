package game.client.ui.widget;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.Texture;
import game.client.ui.screen.WorldSelectScreen;
import game.client.world.SingleplayerWorld;
import org.joml.Vector2f;

public class WorldWidget extends Widget {
    private SingleplayerWorld world;
    private WorldSelectScreen worldSelectScreen;

    private static Texture BORDER_SELECTED_TEXTURE = new Texture("textures/ui/border_selected.png");

    public WorldWidget(SingleplayerWorld world, WorldSelectScreen worldSelectScreen) {
        this.world = world;
        this.worldSelectScreen = worldSelectScreen;
        this.size = new Vector2f(400F, 50F);
    }

    @Override
    public void render(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderText(this.world.name == null ? this.world.worldFolderName : this.world.name, new Vector2f(this.position.x + 10F, this.position.y + this.size.y / 2F - 12F), 24);

        boolean mouseHoveringOver = mouseX > this.position.x && mouseX < this.position.x + this.size.x && mouseY > this.position.y && mouseY < this.position.y + this.size.y;
        if(mouseHoveringOver && KeyboardAndMouseInput.hasLeftClicked()) {
            this.worldSelectScreen.selectWorld(this);
        }

        if(this.worldSelectScreen.getSelectedWorld() == this) {
            this.uiRenderer.renderTexture(BORDER_SELECTED_TEXTURE, this.position, this.size);
        }
    }

    public SingleplayerWorld getWorld() {
        return this.world;
    }
}
