package game.client.ui.screen;

import game.client.SandboxGame;
import game.client.ui.text.Language;
import game.client.world.SingleplayerWorld;
import org.joml.Vector2f;

public class WorldLoadingScreen extends Screen {
    public static String[] splashes = {
            "Broken rendering incoming!",
            "ArrayIndexOutOfBoundsException <3",
            "Functioning collisions first try!",
            "Fall incoming!",
            "Have you tried running into a block really fast?",
            "Cursed shenanigans!",
            "Have you tried turning it off and on again?",
            "Craving RAM",
            "Have you tried playing Minecraft?",
            "Acting is my passion",
            "Breaking water",
            "Eated it all",
            "Graphic design is my passion",
            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            "An attempt was made",
            "Now you're breathing manually",
            "You lost the game",
            "Multiplayer (partially) functional!",
            "Bugs? What are those?",
            "No bugs! (I hope)",
            "593 meters above the sea!",
            "Placeholder text, you shouldn't be able to see this!"
    };

    public int chosenSplash;

    public WorldLoadingScreen() {
        this.chosenSplash = (int) Math.floor(Math.random() * splashes.length);
    }

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        if(this.gameRenderer.world != null && this.gameRenderer.world.ready) {
            this.gameRenderer.setScreen(null);
            SandboxGame.getInstance().getWindow().captureCursor();
            return;
        }

        String loadingText = Language.translate("ui.world.loading");

        if(this.gameRenderer.world instanceof SingleplayerWorld singleplayerWorld) {
            loadingText = loadingText + " (" + singleplayerWorld.chunkLoadingPercentage + "%)";
        }

        this.uiRenderer.renderTextWithShadow(loadingText, new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F + 16), 32, true);
        this.uiRenderer.renderTextWithShadow(splashes[chosenSplash], new Vector2f(this.getScreenWidth() / 2F, this.getScreenHeight() / 2F - 24), 28, true);
    }

    @Override
    public void close() {

    }

    @Override
    public void positionContent() {

    }
}
