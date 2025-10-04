package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.ListWidget;
import game.client.ui.widget.Widget;
import game.client.ui.widget.WorldWidget;
import game.client.world.SingleplayerWorld;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorldSelectScreen extends Screen {
    private Screen prevScreen;
    private WorldWidget selectedWorld;

    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);
    public ButtonWidget loadWorldButton = new ButtonWidget(Language.translate("ui.world_select.load_world"), () -> {
        this.gameRenderer.setScreen(new WorldLoadingScreen());
        Thread worldLoadingThread = new Thread(() -> {
            this.gameRenderer.loadWorld(this.selectedWorld.getWorld());
        });
        worldLoadingThread.start();
    });
    public ButtonWidget createWorldButton = new ButtonWidget(Language.translate("ui.create_world"), () -> {
        this.gameRenderer.setScreen(new WorldCreationScreen(this));
    });

    private ListWidget listWidget = new ListWidget();

    public WorldSelectScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;
        this.renderableWidgets.add(this.closeButton);
        this.renderableWidgets.add(this.loadWorldButton);
        this.renderableWidgets.add(this.listWidget);
        this.renderableWidgets.add(this.createWorldButton);

        ArrayList<SingleplayerWorld> worlds = new ArrayList<>();

        for(File worldFolder : SandboxGame.getInstance().getWorldsFolder().listFiles()) {
            SingleplayerWorld world = new SingleplayerWorld(worldFolder.getName(), SandboxGame.getInstance().getWorldsFolder());
            worlds.add(world);
        }

        List<SingleplayerWorld> sortedWorlds = worlds.stream().sorted((world1, world2) -> {
            if(world1.lastSavedAt > world2.lastSavedAt) {
                return -1;
            } else if(world1.lastSavedAt < world2.lastSavedAt) {
                return 1;
            }

            return 0;
        }).toList();

        for(SingleplayerWorld world : sortedWorlds) {
            this.listWidget.widgets.add(new WorldWidget(world, this));
        }
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.world_select"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
        this.loadWorldButton.disabled = this.selectedWorld == null;
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(this.prevScreen);
    }

    @Override
    public void positionContent() {
        this.closeButton.position = new Vector2f(this.getScreenWidth() - 200F - 50F, 50);
        this.closeButton.size = new Vector2f(200F, 50F);
        this.loadWorldButton.position = new Vector2f(50F, 50);
        this.loadWorldButton.size = new Vector2f(400F, 50F);
        this.listWidget.position = new Vector2f(50F, 150F);
        this.listWidget.size = new Vector2f(this.getScreenWidth() - 100F, this.getScreenHeight() - 100F - 50F - 32F - 50F - 50F);

        this.createWorldButton.size = new Vector2f(400F, 50F);
        this.createWorldButton.position = new Vector2f(this.getScreenWidth() - 50F - 400F, this.listWidget.position.y + this.listWidget.size.y + 50F);

        for(Widget worldWidget : this.listWidget.widgets) {
            worldWidget.size.x = this.listWidget.size.x - 25F;
        }
    }

    public void selectWorld(WorldWidget world) {
        this.selectedWorld = world;
    }

    public WorldWidget getSelectedWorld() {
        return this.selectedWorld;
    }
}
