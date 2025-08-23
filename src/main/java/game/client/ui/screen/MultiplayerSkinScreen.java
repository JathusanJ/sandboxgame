package game.client.ui.screen;

import engine.renderer.Camera;
import game.client.ui.text.Language;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.PlayerInUIWidget;
import game.shared.multiplayer.skin.Skins;
import game.shared.world.creature.OtherPlayer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

public class MultiplayerSkinScreen extends Screen {
    public Screen prevScreen;
    public int currentSkin = 0;
    public ButtonWidget backButton = new ButtonWidget(Language.translate("ui.back"), this::close);
    public ButtonWidget nextButton = new ButtonWidget(Language.translate("ui.multiplayer.skin.next"), () -> {
        this.currentSkin++;
        if(this.currentSkin >= Skins.idToSkin.size()) {
            this.currentSkin = 0;
        }
        this.updateSelectedSkin();
    });
    public ButtonWidget prevButton = new ButtonWidget(Language.translate("ui.multiplayer.skin.previous"), () -> {
        this.currentSkin--;
        if(this.currentSkin < 0) {
            this.currentSkin = Skins.idToSkin.size() - 1;
        }
        this.updateSelectedSkin();
    });
    public PlayerInUIWidget playerInUI = new PlayerInUIWidget(new OtherPlayer(), new Camera());

    public ArrayList<String> idsInOrder = new ArrayList<>();
    public float timeSinceOpening = 0;

    public MultiplayerSkinScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;

        this.renderableWidgets.add(this.backButton);
        this.renderableWidgets.add(this.nextButton);
        this.renderableWidgets.add(this.prevButton);
        this.renderableWidgets.add(this.playerInUI);

        this.idsInOrder.addAll(Skins.idToSkin.keySet());

        this.playerInUI.camera = new Camera();
        this.playerInUI.camera.position = new Vector3f(0F, 1F, 0F);
        this.playerInUI.camera.yaw = 120F;

        this.playerInUI.player.position = new Vector3f(0,0,0);
        this.playerInUI.player.lastPosition = new Vector3f(0,0,0);

        for(int i = 0; i < this.idsInOrder.size(); i++) {
            if(this.idsInOrder.get(i).equals(this.client.settings.skin)) {
                this.currentSkin = i;
                this.updateSelectedSkin();
                break;
            }
        }
    }

    public void updateSelectedSkin() {
        ((OtherPlayer)this.playerInUI.player).skin = Skins.getSkin(this.idsInOrder.get(this.currentSkin));
        this.client.settings.skin = this.idsInOrder.get(this.currentSkin);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.skin"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
        this.uiRenderer.renderTextWithShadow(Language.translate(Skins.idToSkin.get(this.idsInOrder.get(this.currentSkin)).getTranslationId()), new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 70 + 13), 24F, true);
    }

    @Override
    public void renderBeforeWidgets(double deltaTime, int mouseX, int mouseY) {
        this.timeSinceOpening = (float) (this.timeSinceOpening + deltaTime);

        this.playerInUI.camera.position.set(0,0,0);
        this.playerInUI.camera.yaw = (float) (this.playerInUI.camera.yaw + deltaTime * 50F);
        this.playerInUI.camera.pitch = (float) (Math.sin(this.timeSinceOpening * 0.5F) * 30);
        Vector3f direction = this.playerInUI.camera.getDirection();
        this.playerInUI.camera.position.add(direction.mul(-2F)).add(0, 1F, 0);

        this.uiRenderer.renderColoredQuad(this.playerInUI.position, this.playerInUI.size, new Vector4f(0F, 0F, 0F, 0.65F));
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(prevScreen);
        this.client.settings.save();
        this.client.getPlayerProfile().setSkin(Skins.getSkin(this.client.settings.skin));
    }

    @Override
    public void positionContent() {
        this.backButton.position = new Vector2f(this.getScreenWidth() / 2F - 100F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 140);
        this.backButton.size = new Vector2f(200F, 50F);

        this.nextButton.size = new Vector2f(50F, 50F);
        this.nextButton.position = new Vector2f(this.getScreenWidth() / 2F + 150F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 70);
        this.prevButton.size = new Vector2f(50F, 50F);
        this.prevButton.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 32 - 50 - 20 - 300F - 70);

        this.playerInUI.position = new Vector2f(this.getScreenWidth() / 2F - 150F, this.getScreenHeight() - 32 - 50 - 20 - 300F);
        this.playerInUI.size = new Vector2f(300F, 300F);

        this.playerInUI.camera.perspectiveProjection(300, 300, 70F);
    }
}
