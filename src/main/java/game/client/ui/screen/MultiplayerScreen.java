package game.client.ui.screen;

import game.client.multiplayer.GameClient;
import game.client.ui.text.Language;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.TextFieldWidget;
import org.joml.Vector2f;

public class MultiplayerScreen extends Screen {
    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);
    public ButtonWidget multiplayerSettingsButton = new ButtonWidget(Language.translate("ui.multiplayer.settings"), () -> {
        this.gameRenderer.setScreen(new MultiplayerSkinScreen(this));
    });
    public TextFieldWidget serverAddressField = new TextFieldWidget();
    public ButtonWidget connectButton = new ButtonWidget(Language.translate("ui.multiplayer.connect"), () -> {
        this.gameRenderer.setScreen(new ConnectingToServerScreen(this));
        Thread networkingThread = new Thread(() -> {
            try {
                String[] args = serverAddressField.content.split(":");
                if(args.length == 1) {
                    GameClient.connect(args[0], 8080);
                } else if(args.length == 2) {
                    Integer port = null;
                    try {
                        port = Integer.parseInt(args[1]);
                    } catch(Exception ignored) {}

                    if(port != null) {
                        GameClient.connect(args[0], port);
                    }
                }

            } catch (Exception e) {
                this.gameRenderer.setScreen(new DisconnectedScreen(e.getMessage()));
            }}, "client-network");

        networkingThread.start();
    });

    public MultiplayerScreen() {
        this.renderableWidgets.add(closeButton);
        this.renderableWidgets.add(connectButton);
        this.renderableWidgets.add(serverAddressField);
        this.renderableWidgets.add(multiplayerSettingsButton);
        this.serverAddressField.content = "localhost";
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.connectButton.disabled = this.serverAddressField.content.isEmpty();

        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer"), 32, 50, this.getScreenHeight() - 32 - 50);
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.experimental"), 28, this.getScreenWidth() / 2F, this.getScreenHeight() - 32 - 100, true);
        float finalFontSize = Math.min(24, 24 / 800F * this.getScreenWidth());
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.experimental.warning"), finalFontSize, this.getScreenWidth() / 2F, this.getScreenHeight() - 32 - 124, true);
        this.gameRenderer.textRenderer.renderTextWithShadow(Language.translate("ui.multiplayer.address"), this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 275F + 50F);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(new TitleScreen());
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(this.getScreenWidth() - 200F - 50F, 50);
        this.closeButton.size = new Vector2f(200F, 50F);
        this.connectButton.position = new Vector2f(50F, 50);
        this.connectButton.size = new Vector2f(400F, 50F);
        this.multiplayerSettingsButton.size = new Vector2f(400F, 50F);
        this.multiplayerSettingsButton.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 335F);
        this.serverAddressField.size = new Vector2f(400F, 50F);
        this.serverAddressField.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 275F);
    }
}
