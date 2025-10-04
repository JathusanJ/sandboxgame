package game.client.ui.screen;

import game.client.ui.text.Language;
import org.joml.Vector2f;

public class WorldSavingScreen extends WorldLoadingScreen {
    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        if(this.gameRenderer.world == null) {
            this.gameRenderer.setScreen(new TitleScreen());
            return;
        }
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.world.saving"), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16), 32, true);
        this.uiRenderer.renderTextWithShadow(splashes[chosenSplash], new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 28, true);
    }

    @Override
    public void renderBackground(double deltaTime, int mouseX, int mouseY) {
        TitleScreen.backgroundScroll = (TitleScreen.backgroundScroll + (float) deltaTime / 60F) % 1;
        this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 * -TitleScreen.backgroundScroll, 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
        this.uiRenderer.renderTexture(TitleScreen.BACKGROUND_TEXTURE, new Vector2f(this.getScreenHeight() * 4 - (this.getScreenHeight() * 4 * TitleScreen.backgroundScroll), 0), new Vector2f(this.getScreenHeight() * 4, this.getScreenHeight()));
    }
}
