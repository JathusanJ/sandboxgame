package game.client.world.creature;

import engine.input.KeyboardAndMouseInput;
import engine.renderer.Camera;
import game.client.Keybinds;
import game.client.SandboxGame;
import game.client.ui.text.Font;
import game.client.ui.widget.ChatMessage;
import game.shared.multiplayer.skin.Skin;
import game.shared.multiplayer.skin.Skins;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.blocks.Blocks;
import game.shared.world.creature.ItemCreature;
import game.shared.world.creature.Player;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.client.multiplayer.GameClient;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class ClientPlayer extends Player {
    public boolean movedUp = false;

    public ClientPlayer() {
        this.playerProfile = SandboxGame.getInstance().getPlayerProfile();
    }

    @Override
    public void tick() {
        super.tick();

        if(this.breakingCooldown > 0) {
            this.breakingCooldown = this.breakingCooldown - 1;
        }

        if(this.world.getChunk(this.getChunkPosition().x, this.getChunkPosition().y) == null) {
            this.position.set(this.lastPosition);
            this.velocity.set(0,0,0);
        }

        this.lastPosition.set(this.position);
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;

        if(this.blockBreakingProgress != null) {
            if(this.blockBreakingProgress.isDone()) {
                if(KeyboardAndMouseInput.pressingMouseButton(GLFW_MOUSE_BUTTON_1) && !KeyboardAndMouseInput.pressedMouseButton(GLFW_MOUSE_BUTTON_1)) {
                    this.breakingCooldown = 5;
                }
                if(!GameClient.isConnectedToServer) {
                    ItemStack itemStack = this.world.getBlock(this.blockBreakingProgress.blockPosition.x,this.blockBreakingProgress.blockPosition.y,this.blockBreakingProgress.blockPosition.z).getAsDroppedItem(blockBreakingProgress.player, blockBreakingProgress.player.inventory[blockBreakingProgress.hotbarSlot]);
                    if(itemStack != null && itemStack.item != Items.AIR && this.gamemode != Gamemode.CREATIVE) {
                        ItemCreature droppedBlock = new ItemCreature();
                        droppedBlock.representingItemStack = itemStack;
                        droppedBlock.position = new Vector3f(this.blockBreakingProgress.blockPosition.x + 0.5F, this.blockBreakingProgress.blockPosition.y + 0.5F, this.blockBreakingProgress.blockPosition.z + 0.5F);
                        this.world.spawnCreature(droppedBlock);
                    }
                    this.world.setBlock(this.blockBreakingProgress.blockPosition.x, this.blockBreakingProgress.blockPosition.y, this.blockBreakingProgress.blockPosition.z, Blocks.AIR);
                    this.blockBreakingProgress = null;
                }
            } else {
                this.blockBreakingProgress.breakingTicks = this.blockBreakingProgress.breakingTicks + 1;
            }
        }
    }

    @Override
    public void remove() {
        // Let's not remove the local player lol
    }

    public void resetMovement() {
        this.velocity.x = 0F;
        this.velocity.z = 0F;
        if(this.flying) {
            this.velocity.y = 0F;
        }
        this.movedUp = false;
    }

    public void handleMovement(float deltaTime, Vector3f forwards, Vector3f right) {
        if(Keybinds.WALK_FORWARDS.pressing()) {
            this.velocity.add(forwards.mul(1, 0, 1, new Vector3f()).normalize().mul(SandboxGame.getInstance().getGameRenderer().cameraSpeed));
        }
        if(Keybinds.WALK_BACKWARDS.pressing()) {
            this.velocity.sub(forwards.mul(1, 0, 1, new Vector3f()).normalize().mul(SandboxGame.getInstance().getGameRenderer().cameraSpeed));
        }
        if(Keybinds.WALK_LEFT.pressing()) {
            this.velocity.sub(right.mul(1, 0, 1, new Vector3f()).normalize().mul(SandboxGame.getInstance().getGameRenderer().cameraSpeed));
        }
        if(Keybinds.WALK_RIGHT.pressing()) {
            this.velocity.add(right.mul(1, 0, 1, new Vector3f()).normalize().mul(SandboxGame.getInstance().getGameRenderer().cameraSpeed));
        }
        if(Keybinds.FLY_DOWN.pressing() && (this.flying || this.isInLiquid())) {
            this.velocity.y = -7F;
        }
        if(Keybinds.TOGGLE_FLIGHT.pressed() && this.gamemode == Gamemode.CREATIVE) {
            this.flying = !this.flying;
        }
        if(Keybinds.JUMP.pressing() && (this.isOnGround || this.flying || this.isInLiquid())) {
            this.velocity.y = 7F;
            if(this.isInLiquid()) {
                this.velocity.y = 2F;
            }
            this.movedUp = true;
        }
    }

    public void applyPhysics(float deltaTime) {
        if(!this.movedUp && !this.flying) {
            float acceleration = -9.81F * 2F;
            boolean isInWater = this.isInLiquid();
            if(isInWater) {
                acceleration = -2F;
            }
            this.velocity.y = this.velocity.y + acceleration * deltaTime;

            if(isInWater) {
                this.velocity.y = Math.max(-4, this.velocity.y);
            }
        }
        this.isOnGround = false;
        this.position.add(this.velocity.x * deltaTime, 0F, this.velocity.z * deltaTime);
        this.checkAndHandleCollision(false);
        this.position.add(0F, this.velocity.y * deltaTime, 0F);
        this.checkAndHandleCollision(true);
    }

    @Override
    public void switchToHotbarSlot(int slot) {
        super.switchToHotbarSlot(slot);
        /*if(GameClient.isConnectedToServer) {
            GameClientHandler.sendPacket(new SetHotbarSlotPacket(slot));
        }*/
    }

    @Override
    public void sendChatMessage(String message) {
        SandboxGame.getInstance().logger.info("[CHAT] {}", message);

        float textWidth = Font.getTextWidth(message, 24);
        ArrayList<String> lines = new ArrayList<>();
        while(textWidth > 400) {
            int letters = 0;
            float currentSize = 0;
            for(int i = 0; i < message.length(); i++) {
                float characterWidth = Font.getCharacterWidth(String.valueOf(message.charAt(i)), 24);
                if(characterWidth + currentSize > 400) {
                    textWidth = textWidth - currentSize;
                    lines.add(message.substring(0, letters));
                    message = message.substring(letters);
                    break;
                } else {
                    letters++;
                    currentSize = currentSize + characterWidth;
                }
            }
            textWidth = textWidth - currentSize;
        }

        if(!message.isEmpty()) {
            lines.add(message);
        }

        for(String line : lines) {
            SandboxGame.getInstance().getGameRenderer().chatRenderer.add(new ChatMessage(line));
        }
    }

    @Override
    public void save(WrappedJsonObject json) {
        super.save(json);

        Camera camera = SandboxGame.getInstance().getGameRenderer().camera;

        WrappedJsonObject cameraRotation = new WrappedJsonObject();
        cameraRotation.put("pitch", camera.pitch);
        cameraRotation.put("yaw", camera.yaw);

        json.put("camera", cameraRotation);
    }

    @Override
    public void load(WrappedJsonObject json) {
        super.load(json);

        Camera camera = SandboxGame.getInstance().getGameRenderer().camera;
        camera.pitch = json.getObject("camera").getFloat("pitch");
        camera.yaw = json.getObject("camera").getFloat("yaw");
    }

    @Override
    public void respawn() {
        Camera camera = SandboxGame.getInstance().getGameRenderer().camera;
        camera.yaw = 0F;
        camera.pitch = 0F;
        super.respawn();
    }

    @Override
    public Skin getSkin() {
        return Skins.getSkin(SandboxGame.getInstance().settings.skin);
    }
}
