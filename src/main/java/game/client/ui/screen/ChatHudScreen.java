package game.client.ui.screen;

import engine.input.KeyboardAndMouseInput;
import game.client.SandboxGame;
import game.client.ui.widget.ChatMessage;
import game.client.ui.widget.ChatTextInputWidget;
import game.client.networking.GameClient;
import game.client.networking.GameClientHandler;
import game.logic.world.creature.Player;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.networking.packets.ChatMessagePacket;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;

public class ChatHudScreen extends Screen {
    public ChatTextInputWidget textInput;

    public ChatHudScreen() {
        this.textInput = new ChatTextInputWidget();
        this.gameRenderer.setCurrentTextField(this.textInput);
    }

    @Override
    public void renderBackground(double deltaTime, int mouseX, int mouseY) {}

    @Override
    public void renderContents(double deltaTime, int mouseX, int mouseY) {
        this.uiRenderer.renderColoredQuad(new Vector2f(25F, 75), new Vector2f(this.getScreenWidth() - 50F, 25F), new Vector4f(0,0,0,0.75F));
        this.textInput.render(deltaTime, mouseX, mouseY);

        if(KeyboardAndMouseInput.pressedKey(GLFW_KEY_ENTER)) {

            if(GameClient.isConnectedToServer) {
                ChatMessagePacket packet = new ChatMessagePacket(this.textInput.content);
                GameClientHandler.sendPacket(packet);
            } else {
                if(this.textInput.content.startsWith("/") && this.gameRenderer.world.commandsEnabled) {
                    if(this.textInput.content.startsWith("/respawn")) {
                        SandboxGame.getInstance().getGameRenderer().player.position.x = 0;
                        SandboxGame.getInstance().getGameRenderer().player.position.y = 100;
                        SandboxGame.getInstance().getGameRenderer().player.position.z = 0;
                    } else if(this.textInput.content.startsWith("/teleport")) {
                        String[] arguments = this.textInput.content.split(" ");
                        if (arguments.length == 4) {
                            SandboxGame.getInstance().getGameRenderer().player.position.x = Integer.parseInt(arguments[1]);
                            SandboxGame.getInstance().getGameRenderer().player.position.y = Integer.parseInt(arguments[2]);
                            SandboxGame.getInstance().getGameRenderer().player.position.z = Integer.parseInt(arguments[3]);
                            SandboxGame.getInstance().getGameRenderer().player.lastPosition.set(SandboxGame.getInstance().getGameRenderer().player.position);
                        } else {
                            this.gameRenderer.player.sendChatMessage("Expected 3 arguments, got " + (arguments.length - 1) + " arguments instead");
                        }
                    } else if(this.textInput.content.startsWith("/give")) {
                        String[] arguments = this.textInput.content.split(" ");
                        if (arguments.length == 3 || arguments.length == 2) {
                            Item item = Items.idToItem.get(arguments[1]);
                            if (item != null) {
                                ItemStack itemStack = new ItemStack(item);
                                if (arguments.length == 3) {
                                    try {
                                        int amount = Integer.parseInt(arguments[2]);
                                        if (amount < 1) {
                                            this.gameRenderer.player.sendChatMessage("Amount must be larger than 0");
                                        } else if (amount > 64) {
                                            this.gameRenderer.player.sendChatMessage("Amount must be smaller than 65");
                                        } else {
                                            itemStack.amount = amount;
                                            this.gameRenderer.player.putInInventory(itemStack);
                                        }
                                    } catch (NumberFormatException e) {
                                        this.gameRenderer.player.sendChatMessage("Amount isn't a valid integer");
                                    }
                                } else {
                                    itemStack.amount = 1;
                                    this.gameRenderer.player.putInInventory(itemStack);
                                }
                            } else {
                                this.gameRenderer.player.sendChatMessage("Specified item id doesn't exist");
                            }
                        } else {
                            this.gameRenderer.player.sendChatMessage("Expected 1 or 2 arguments, got " + (arguments.length - 1) + " arguments instead");
                        }
                    } else if(this.textInput.content.startsWith("/gamemode")) {
                        String[] arguments = this.textInput.content.split(" ");
                        if (arguments.length != 2) {
                            this.gameRenderer.player.sendChatMessage("Expected 1 argument, got " + (arguments.length - 1) + " arguments instead");
                        } else {
                            try {
                                Player.Gamemode gamemode = Player.Gamemode.valueOf(arguments[1].toUpperCase());
                                if (gamemode != null) {
                                    this.gameRenderer.player.setGamemode(gamemode);
                                    this.gameRenderer.player.sendChatMessage("Changed gamemode to " + this.gameRenderer.player.gamemode.name());
                                }
                            } catch (IllegalArgumentException e) {
                                this.gameRenderer.player.sendChatMessage("Invalid gamemode \"" + arguments[1] + "\"! Expected SURVIVAL or CREATIVE");
                            }
                        }
                    } else if(this.textInput.content.startsWith("/kill")) {
                        this.gameRenderer.player.kill();
                    } else {
                        this.gameRenderer.player.sendChatMessage("Unknown command \"" + this.textInput.content + "\"");
                    }
                } else {
                    this.gameRenderer.player.sendChatMessage(SandboxGame.getInstance().getPlayerProfile().getUsername() + ": " + this.textInput.content);
                }
            }
            this.close();
        }
    }

    @Override
    public void close() {
        this.gameRenderer.setScreen(null);
        this.gameRenderer.setCurrentTextField(null);
    }

    @Override
    public void positionContent() {
        this.textInput.size.x = this.getScreenWidth() - 50;
        this.textInput.size.y = 25;
        this.textInput.position.x = 25;
        this.textInput.position.y = 75;
    }
}
