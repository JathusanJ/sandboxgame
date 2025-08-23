package game.shared.world.creature;

import game.shared.multiplayer.ByteBufPacketDecoder;
import game.shared.multiplayer.skin.Skin;
import game.shared.util.PlayerProfile;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.blocks.Blocks;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Creature {
    public PlayerProfile playerProfile;
    public int currentHotbarSlot = 0;
    public ItemStack[] inventory = new ItemStack[4 * 9];
    public Gamemode gamemode = Gamemode.SURVIVAL;
    public BlockBreakingProgress blockBreakingProgress;
    public boolean flying = false;
    public int breakingCooldown = 0;
    public int invincibilityTimer = 0;

    public Player() {
        for(int i = 0; i < this.inventory.length; i++) {
           this.inventory[i] = new ItemStack(Items.AIR);
        }

        this.size = new Vector3f(0.5F, 1.9F, 0.5F);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        for (int i = 0; i < this.inventory.length; i++) {
            if(this.inventory[i].getItem() != Items.AIR) {
                ItemCreature droppedItem = new ItemCreature();
                droppedItem.representingItemStack = this.inventory[i];
                droppedItem.velocity = new Vector3f(this.world.random.nextFloat() * 4F - 2F, 2F, this.world.random.nextFloat() * 4F - 2F);
                droppedItem.setPosition(this.position.x, this.position.y, this.position.z);
                this.world.spawnCreature(droppedItem);
            }
        }

        this.clearInventory();
        this.respawn();

        this.sendChatMessage(damageSource.getTranslated(this.playerProfile.getUsername()));
    }

    public void clearInventory() {
        for (int i = 0; i < this.inventory.length; i++) {
            this.inventory[i] = new ItemStack(Items.AIR);
        }
    }

    public void respawn() {
        Vector3f spawnLocation = this.world.findPossibleSpawnLocation();
        this.setPosition(spawnLocation.x, spawnLocation.y, spawnLocation.z);
        this.health = this.maxHealth;
        this.flying = false;
        this.currentHotbarSlot = 0;
    }

    @Override
    public void damage(float amount, DamageSource damageSource) {
        if(this.gamemode == Gamemode.SURVIVAL && this.invincibilityTimer == 0) {
            super.damage(amount, damageSource);
            this.invincibilityTimer = 40;
        }
    }

    @Override
    public void tick() {
        if(this.invincibilityTimer > 0) {
            this.invincibilityTimer = this.invincibilityTimer - 1;
        } else {
            Vector3i blockPosition1 = this.getBlockPosition();
            if(this.world.getBlock(blockPosition1) == Blocks.LAVA) {
                this.damage(3F, DamageSource.FIRE);
            } else {
                Vector3i blockPosition2 = this.getBlockPosition().add(0, 1, 0);
                if(this.world.getBlock(blockPosition2) == Blocks.LAVA) {
                    this.damage(3F, DamageSource.FIRE);
                }
            }
        }
    }

    @Override
    public void save(WrappedJsonObject json) {
        super.save(json);
        json.put("currentHotbarSlot", this.currentHotbarSlot);
        json.put("flying", this.flying);
        json.put("gamemode", this.gamemode);
        ArrayList<HashMap<String, Object>> inventory = new ArrayList<>();

        for(int i = 0; i < this.inventory.length; i++) {
            WrappedJsonObject slot = new WrappedJsonObject();
            if(this.inventory[i] != null) {
                 this.inventory[i].saveAsJson(slot);
            } else {
                new ItemStack(Items.AIR).saveAsJson(slot);
            }
            inventory.add(slot.children);
        }

        json.put("inventory", inventory);
    }

    @Override
    public void load(WrappedJsonObject json) {
        super.load(json);
        this.currentHotbarSlot = json.getInt("currentHotbarSlot");
        this.flying = json.getBoolean("flying");
        this.gamemode = Gamemode.valueOf(json.getString("gamemode"));

        ArrayList<WrappedJsonObject> inventory = (ArrayList<WrappedJsonObject>) json.getList("inventory");

        for(int i = 0; i < inventory.size(); i++) {
            this.inventory[i] = ItemStack.readFromJson(inventory.get(i));
        }
    }

    @Override
    public void writeSpawnPacket(ByteBuf buffer) {
        super.writeSpawnPacket(buffer);
        ByteBufPacketDecoder.writeString(buffer, this.getSkin().id);
    }

    public void setGamemode(Gamemode gamemode) {
        this.gamemode = gamemode;
        if(gamemode == Gamemode.SURVIVAL) {
            this.flying = false;
        }
    }

    public enum Gamemode {
        SURVIVAL,
        CREATIVE
    }

    public static class BlockBreakingProgress {
        public int breakingTicks = 0;
        public Vector3i blockPosition;
        public Player player;
        public int hotbarSlot;

        public BlockBreakingProgress(Vector3i blockPosition, Player player) {
            this.blockPosition = blockPosition;
            this.player = player;
            this.hotbarSlot = player.currentHotbarSlot;
        }

        public boolean isDone() {
            return this.player.gamemode == Gamemode.CREATIVE || breakingTicks >= this.player.world.getBlock(this.blockPosition.x, this.blockPosition.y, this.blockPosition.z).getBlockBreakingTicks(this.player, this.player.inventory[this.player.currentHotbarSlot]);
        }

        public int getTotalBreakingTicks() {
            return this.player.world.getBlock(this.blockPosition.x, this.blockPosition.y, this.blockPosition.z).getBlockBreakingTicks(this.player, this.player.inventory[this.player.currentHotbarSlot]);
        }
    }

    public void switchToHotbarSlot(int slot) {
        this.currentHotbarSlot = slot;
    }

    public boolean isInLiquid() {
        if(this.world.getChunk(this.getChunkPosition().x, this.getChunkPosition().y) == null) return false;

        return this.world.getBlock((int) Math.floor(this.position.x), (int) Math.floor(this.position.y), (int) Math.floor(this.position.z)).isLiquid()
                || this.world.getBlock((int) Math.floor(this.position.x), (int) Math.floor(this.position.y) + 1, (int) Math.floor(this.position.z)).isLiquid();
    }

    public void putInInventory(ItemStack stack) {
        // Find the nearest slot with the same item
        for (int i = 0; i < this.inventory.length; i++) {
            ItemStack itemstack = this.inventory[i];
            if(stack.getItem() == itemstack.getItem() && itemstack.amount < 64) {
                int amountToTransfer = Math.min(stack.amount, 64 - itemstack.amount);
                itemstack.setAmount(itemstack.amount + amountToTransfer);
                stack.setAmount(stack.amount - amountToTransfer);
            }

            if(stack.amount <= 0) return;
        }
        // Find the nearest empty slot
        for (int i = 0; i < this.inventory.length; i++) {
            if(this.inventory[i].getItem() == Items.AIR || this.inventory[i].amount == 0) {
                this.inventory[i] = stack;
                return;
            }
        }

        // And drop as a last measure
        ItemCreature itemCreature = new ItemCreature();
        itemCreature.representingItemStack = stack;
        itemCreature.setPosition(this.position.x, this.position.y + 1.5F, this.position.z);
        this.world.spawnCreature(itemCreature);
    }

    public void sendChatMessage(String message) {

    }

    public Skin getSkin() {
        return this.playerProfile.getSkin();
    }
}
