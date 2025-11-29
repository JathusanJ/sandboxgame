package game.client.ui.screen;

import game.client.ui.text.Language;
import game.shared.multiplayer.skin.Skins;
import org.joml.Vector2f;

public class SkinSetupScreen extends MultiplayerSkinScreen {
    public SkinSetupScreen(Screen prevScreen) {
        super(prevScreen);
        this.backButton.setText(Language.translate("ui.next"));
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.setup.skin"), 32, 50, this.getScreenHeight() - 32 - 50);
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate(Skins.idToSkin.get(this.idsInOrder.get(this.currentSkin)).getTranslationId()), this.getScreenWidth() / 2F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 70 + 13, true);
    }
}
