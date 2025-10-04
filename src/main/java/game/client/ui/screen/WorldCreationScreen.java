package game.client.ui.screen;

import game.client.ui.text.Language;
import game.client.ui.widget.BooleanToggleWidget;
import game.client.ui.widget.ButtonWidget;
import game.client.ui.widget.EnumSelectWidget;
import game.client.ui.widget.TextFieldWidget;
import game.client.world.SingleplayerWorld;
import game.shared.world.World;
import org.joml.Vector2f;

import java.io.File;
import java.util.random.RandomGeneratorFactory;

public class WorldCreationScreen extends Screen {
    public Screen prevScreen;
    public ButtonWidget closeButton = new ButtonWidget(Language.translate("ui.close"), this::close);
    public ButtonWidget createWorldButton = new ButtonWidget(Language.translate("ui.create_world"), () -> {
        if(this.worldNameTextField.content.trim().isEmpty()) return;

        String worldName = this.worldNameTextField.content.replaceAll("[^a-zA-Z0-9\\\\._ ]+", "_");

        if(new File(this.client.getWorldsFolder(), worldName).exists()) {
            return;
        }

        int worldSeed;
        if(this.worldSeedTextField.content.isEmpty()) {
            worldSeed = RandomGeneratorFactory.getDefault().create().nextInt();
        } else {
            try {
                worldSeed = Integer.parseInt(this.worldSeedTextField.content);
            } catch (NumberFormatException e) {
                worldSeed = this.worldSeedTextField.content.hashCode();
            }
        }
        this.gameRenderer.setScreen(new WorldLoadingScreen());
        // "Variable used in lambda expression should be final or effectively final" or something
        int finalWorldSeed = worldSeed;
        // https://stackoverflow.com/a/17745189
        SingleplayerWorld world = new SingleplayerWorld(this.worldNameTextField.content, finalWorldSeed, this.worldTypeEnumSelectWidget.getValue(), worldName, this.client.getWorldsFolder());
        world.commandsEnabled = this.commandsEnabled;
        world.writeWorldInfo();
        Thread worldLoadingThread = new Thread(() -> {
            this.gameRenderer.loadWorld(world);
        });
        worldLoadingThread.start();
    });
    public TextFieldWidget worldNameTextField = new TextFieldWidget();
    public TextFieldWidget worldSeedTextField = new TextFieldWidget();
    public EnumSelectWidget<World.WorldType> worldTypeEnumSelectWidget = new EnumSelectWidget<>("World type", World.WorldType.class);
    public BooleanToggleWidget commandsEnabledWidget = new BooleanToggleWidget(Language.translate("ui.create_world.commands_enabled"), (value) -> this.commandsEnabled = value, () -> this.commandsEnabled);
    public boolean commandsEnabled = false;

    public WorldCreationScreen(Screen prevScreen) {
        this.prevScreen = prevScreen;

        this.renderableWidgets.add(this.closeButton);
        this.renderableWidgets.add(this.createWorldButton);
        this.renderableWidgets.add(this.worldNameTextField);
        this.renderableWidgets.add(this.worldSeedTextField);
        this.renderableWidgets.add(this.worldTypeEnumSelectWidget);
        this.renderableWidgets.add(commandsEnabledWidget);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.create_world"), new Vector2f(50, this.getScreenHeight() - 32 - 50), 32);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.create_world.world_name"), new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 125F), 24);
        this.uiRenderer.renderTextWithShadow(Language.translate("ui.create_world.world_seed"), new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 225F), 24);
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(prevScreen);
    }

    @Override
    public void positionContent() {
        this.createWorldButton.size = new Vector2f(400F, 50F);
        this.createWorldButton.position = new Vector2f(50F, 50F);
        this.closeButton.position = new Vector2f(this.getScreenWidth() - 200F - 50F, 50);
        this.closeButton.size = new Vector2f(200F, 50F);
        this.worldNameTextField.size = new Vector2f(400F, 50F);
        this.worldNameTextField.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 175F);
        this.worldSeedTextField.size = new Vector2f(400F, 50F);
        this.worldSeedTextField.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 275F);
        this.worldTypeEnumSelectWidget.size = new Vector2f(400F, 50F);
        this.worldTypeEnumSelectWidget.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 350F);
        this.commandsEnabledWidget.size = new Vector2f(400F, 50F);
        this.commandsEnabledWidget.position = new Vector2f(this.getScreenWidth() / 2F - 200F, this.getScreenHeight() - 425F);
    }
}
