package engine;

import engine.renderer.Window;
import engine.sound.Sounds;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEngine {
    private static Game currentGame;
    private static Window window;
    public static Logger logger = LoggerFactory.getLogger("GameEngine");

    public static void run(Game game, String[] arguments) {
        try {
            if (currentGame != null) throw new IllegalStateException("A game is already being run");
            currentGame = game;
            StartupArguments parsedArguments = new StartupArguments(arguments);

            game.initialize(parsedArguments);

            logger.info("Initialized {} {}", game.getGameName(), game.getGameVersion());

            window = new Window(parsedArguments.windowWidth, parsedArguments.windowHeight, game.getGameName() + " " + game.getGameVersion());
            window.init();

            game.postWindowInitialization();

            window.show();
            window.loop();
        } catch(Exception e) {
            logger.error("Error running game", e);

            Sounds.unload();

            throw e;
        }

        Sounds.unload();
    }

    public static Game getGame() {
        return currentGame;
    }

    public static Window getWindow() {
        return window;
    }
}
