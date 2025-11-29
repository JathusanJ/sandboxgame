package game.client.ui.screen;

import game.client.ui.text.Language;
import org.joml.Vector2f;

public class StaticWorldSavingScreen extends Screen {
    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.world.saving"), 32, this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16, true);
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {

    }

    @Override
    public void renderBackground(double deltaTime, int mouseX, int mouseY) {
        TitleScreen.backgroundScroll = (TitleScreen.backgroundScroll + (float) deltaTime / 60F) % 1;
        this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 * -TitleScreen.backgroundScroll, 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
        this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 - (this.getScreenHeight() * 4 * TitleScreen.backgroundScroll), 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
    }
}
