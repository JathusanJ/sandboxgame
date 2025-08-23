package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.TextFieldWidget;
import org.joml.Vector2f;

public class UsernameSetupScreen extends Screen {
    public Screen nextScreen;
    public TextFieldWidget usernameField = new TextFieldWidget();
    public ButtonWidget proceedButton = new ButtonWidget(Language.translate("ui.next"), this::close);

    public UsernameSetupScreen(Screen nextScreen) {
        this.nextScreen = nextScreen;

        this.renderableWidgets.add(this.usernameField);
        this.renderableWidgets.add(this.proceedButton);

        this.usernameField.content = this.client.settings.username;
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.setup.username"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.setup.username.field"), new Vector2f(this.getScreenWidth() / 2F - 150F, this.getScreenHeight() - 300F), 24);
    }

    @Override
    public void close() {
        if(this.usernameField.content.trim().length() < 3) {
            return;
        }
        this.client.settings.username = this.usernameField.content.trim();
        this.client.getPlayerProfile().setUsername(this.client.settings.username);
        this.gameRenderer.setScreen(this.nextScreen);
    }

    @Override
    public void positionContent() {
        this.proceedButton.position = new Vector2f(this.getScreenWidth() / 2F - 100F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 140);
        this.proceedButton.size = new Vector2f(200F, 50F);

        this.usernameField.position = new Vector2f(this.getScreenWidth() / 2F - 150F, this.getScreenHeight() - 300 - 50);
        this.usernameField.size = new Vector2f(300F, 50F);
    }
}
