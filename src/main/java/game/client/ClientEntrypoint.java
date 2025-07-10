package game.client;

import engine.GameEngine;

public class ClientEntrypoint {
    public static void main(String[] args){
        GameEngine.run(new SandboxGame(), args);
    }
}
