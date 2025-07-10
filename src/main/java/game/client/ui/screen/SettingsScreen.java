package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.ui.widget.BooleanToggleWidget;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.SliderWidget;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwSwapInterval;

public class SettingsScreen extends Screen {
    public SliderWidget renderDistanceSlider = new SliderWidget(Language.translate("ui.screen.settings.render_distance"), SliderWidget::displayValueAsIs, (value) -> {
        SandboxGame.getInstance().settings.renderDistance = value.intValue();
    });

    public SliderWidget fovSlider = new SliderWidget(Language.translate("ui.screen.settings.field_of_view"), SliderWidget::displayValueAsIs, (value) -> {
        this.gameRenderer.camera.perspectiveProjection(this.getScreenWidth(), this.getScreenHeight(), value);
    });

    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);

    public BooleanToggleWidget vsyncToggle = new BooleanToggleWidget(Language.translate("ui.screen.settings.vsync"), (value) -> {
        SandboxGame.getInstance().settings.vsync = value;
        glfwSwapInterval(SandboxGame.getInstance().settings.vsync ? 1 : 0);
    }, () -> SandboxGame.getInstance().settings.vsync);

    public Screen prevScreen;

    public SettingsScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderDistanceSlider.value = SandboxGame.getInstance().settings.renderDistance;
        this.renderDistanceSlider.maxValue = 32F;
        this.renderDistanceSlider.minValue = 4F;
        this.fovSlider.value = this.gameRenderer.camera.fov;
        this.fovSlider.maxValue = 170F;
        this.fovSlider.minValue = 30F;

        this.renderableWidgets.add(this.fovSlider);
        this.renderableWidgets.add(this.vsyncToggle);
        this.renderableWidgets.add(this.renderDistanceSlider);
        this.renderableWidgets.add(this.closeButton);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.screen.settings"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(prevScreen);
        SandboxGame.getInstance().settings.save();
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
    }
}
