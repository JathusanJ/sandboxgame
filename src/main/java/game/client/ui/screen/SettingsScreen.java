package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.ui.text.Text;
import game.client.ui.widget.BooleanToggleWidget;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.SliderWidget;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public class SettingsScreen extends Screen {
    public SliderWidget renderDistanceSlider = new SliderWidget(new Text.Translated("ui.screen.settings.render_distance"), SliderWidget::displayValueAsIs, (value) -> {
        this.client.settings.renderDistance = value.intValue();
    });

    public SliderWidget fovSlider = new SliderWidget(new Text.Translated("ui.screen.settings.field_of_view"), SliderWidget::displayValueAsIs, (value) -> {
        this.gameRenderer.camera.perspectiveProjection(this.getScreenWidth(), this.getScreenHeight(), value);
    });

    public ButtonWidget closeButton = new ButtonWidget(new Text.Translated("ui.close"), this::close);

    public BooleanToggleWidget vsyncToggle = new BooleanToggleWidget(Language.translate("ui.screen.settings.vsync"), (value) -> {
        this.client.settings.vsync = value;
        glfwSwapInterval(this.client.settings.vsync ? 1 : 0);
    }, () -> this.client.settings.vsync);

    public ButtonWidget languageButton = new ButtonWidget(new Text.Translated("ui.language_select"), () -> {
        this.gameRenderer.setScreen(new LanguageSelectScreen(this));
    });

    public ButtonWidget creditsButton = new ButtonWidget(new Text.Translated("ui.credits"), () -> {
        this.gameRenderer.setScreen(new CreditsScreen(this));
    });

    public ButtonWidget changeUsernameSkin = new ButtonWidget(new Text.Translated("ui.setup.open"), () -> {
        this.gameRenderer.setScreen(new UsernameSetupScreen(new SkinSetupScreen(this)));
    });

    public ButtonWidget controlsButton = new ButtonWidget(new Text.Translated("ui.controls"), () -> {
        this.gameRenderer.setScreen(new ControlsScreen(this));
    });

    public Screen prevScreen;

    public SettingsScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderDistanceSlider.value = this.client.settings.renderDistance;
        this.renderDistanceSlider.maxValue = 32F;
        this.renderDistanceSlider.minValue = 4F;
        this.fovSlider.value = this.gameRenderer.camera.fov;
        this.fovSlider.maxValue = 170F;
        this.fovSlider.minValue = 30F;

        this.renderableWidgets.add(this.fovSlider);
        this.renderableWidgets.add(this.vsyncToggle);
        this.renderableWidgets.add(this.renderDistanceSlider);
        this.renderableWidgets.add(this.closeButton);
        this.renderableWidgets.add(this.languageButton);
        this.renderableWidgets.add(this.creditsButton);
        this.renderableWidgets.add(this.changeUsernameSkin);
        this.renderableWidgets.add(this.controlsButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.settings"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prevScreen);
        this.client.settings.save();
    }

    @Override
    public void positionContent() {
        this.renderDistanceSlider.position = new Vector2f( 50, this.getScreenHeight() - 50 - 32 - 25 - 50);
        this.renderDistanceSlider.size = new Vector2f(400F, 50F);
        this.closeButton.position = new Vector2f(50, 50);
        this.closeButton.size = new Vector2f(400F, 50F);
        this.fovSlider.position = new Vector2f( 50, this.getScreenHeight() - 50 - 32 - 25 - 50 - 75);
        this.fovSlider.size = new Vector2f(400F, 50F);
        this.vsyncToggle.position = new Vector2f(50, this.getScreenHeight() - 50 - 32 - 25 - 50 - 150);
        this.vsyncToggle.size = new Vector2f(400F, 50F);
        this.languageButton.position = new Vector2f(50, this.getScreenHeight() - 50 - 32 - 25 - 50 - 225);
        this.languageButton.size = new Vector2f(400F, 50F);
        this.changeUsernameSkin.position = new Vector2f(50, this.getScreenHeight() - 50 - 32 - 25 - 50 - 225 - 75);
        this.changeUsernameSkin.size = new Vector2f(400F, 50F);
        this.creditsButton.position = new Vector2f(475F, this.getScreenHeight() - 50 - 32 - 25 - 50 - 225 - 75);
        this.creditsButton.size = new Vector2f(300F, 50F);
        this.controlsButton.position = new Vector2f(475F, this.getScreenHeight() - 50 - 32 - 25 - 50 - 225);
        this.controlsButton.size = new Vector2f(300F, 50F);
    }
}
