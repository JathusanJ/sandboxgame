package game.shared.world.creature;

import game.server.world.ServerPlayer;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.shared.multiplayer.ByteBufPacketDecoder;
import game.client.multiplayer.GameClient;
import game.shared.multiplayer.packets.SetInventorySlotContentPacket;
import io.netty.buffer.ByteBuf;
import org.joml.Vector3f;

public class ItemCreature extends Creature {
    public int ticksRemaining = 6000;
    public int pickupDelay = 20;
    public ItemStack representingItemStack = new ItemStack(Items.AIR);

    public ItemCreature() {
        this.size = new Vector3f(0.5F);
    }

    @Override
    public void tick() {
        super.tick();

        ticksRemaining = ticksRemaining - 1;

        if(ticksRemaining <= 0 || this.representingItemStack.getItem() == Items.AIR || this.representingItemStack.amount == 0) {
            // Despawn
            this.remove();
            return;
        }

        this.movementTick();

        this.pitch = this.pitch + 0.01F;

        if(GameClient.isConnectedToServer) return;

        // Check for nearby items to merge with
        for(Creature creature : this.world.creatures) {
            if(creature instanceof ItemCreature itemCreature && creature != this && !creature.markedForRemoval) {
                if(itemCreature.representingItemStack.getItem() == this.representingItemStack.getItem() && 64 - itemCreature.representingItemStack.amount >= this.representingItemStack.amount && this.position.sub(itemCreature.position, new Vector3f()).length() <= 1F) {
                    if(itemCreature.ticksRemaining > this.ticksRemaining) {
                        itemCreature.representingItemStack.increaseBy(this.representingItemStack.amount);
                        this.remove();
                        return;
                    } else {
                        this.representingItemStack.increaseBy(itemCreature.representingItemStack.amount);
                        itemCreature.remove();
                    }
                }
            }
        }

        // Check if the player is nearby for a pick up
        if(pickupDelay > 0) {
            pickupDelay = pickupDelay - 1;
        } else {
            Player player = null;
            float distance = 10F;

            for(int i = 0; i < this.world.creatures.size(); i++) {
                Creature creature = this.world.creatures.get(i);
                if(creature instanceof Player playerToCheck) {
                    float playerDistance = this.position.sub(playerToCheck.position, new Vector3f()).length();
                    if(player == null) {
                        player = playerToCheck;
                        distance = playerDistance;
                    } else if(playerDistance < distance) {
                        player = playerToCheck;
                        distance = playerDistance;
                    }
                }
            }

            if(player == null) return;

            if(distance <= 2F) {
                // Find the nearest slot with the same item
                for (int i = 0; i < player.inventory.length; i++) {
                    ItemStack itemstack = player.inventory[i];
                    if(this.representingItemStack.getItem() == itemstack.getItem() && itemstack.amount < 64) {
                        int amountToTransfer = Math.min(this.representingItemStack.amount, 64 - itemstack.amount);
                        itemstack.setAmount(itemstack.amount + amountToTransfer);
                        this.representingItemStack.setAmount(this.representingItemStack.amount - amountToTransfer);
                        if(player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.sendPacket(new SetInventorySlotContentPacket(player, i));
                        }
                    }
                    if(this.representingItemStack.amount <= 0) break;
                }
                if(!(this.representingItemStack.getItem() == Items.AIR || this.representingItemStack.amount == 0)) {
                    // Find the nearest empty slot
                    for (int i = 0; i < player.inventory.length; i++) {
                        ItemStack itemstack = player.inventory[i];
                        if(itemstack.getItem() == Items.AIR) {
                            itemstack.setItem(this.representingItemStack.getItem());
                            itemstack.setAmount(0);
                            int amountToTransfer = Math.min(this.representingItemStack.amount, 64 - itemstack.amount);
                            itemstack.setAmount(itemstack.amount + amountToTransfer);
                            this.representingItemStack.setAmount(this.representingItemStack.amount - amountToTransfer);
                            if(player instanceof ServerPlayer serverPlayer) {
                                serverPlayer.sendPacket(new SetInventorySlotContentPacket(player, i));
                            }
                        }
                        if(this.representingItemStack.amount <= 0) break;
                    }
                }
            }
        }
    }

    @Override
    public void damage(float amount) {
        // No damage until I add damage sources
    }

    @Override
    public void writeSpawnPacket(ByteBuf buffer) {
        super.writeSpawnPacket(buffer);
        ByteBufPacketDecoder.writeString(buffer, this.representingItemStack.getItem().id);
        buffer.writeByte(this.representingItemStack.amount);
    }

    @Override
    public void readSpawnPacket(ByteBuf buffer) {
        super.readSpawnPacket(buffer);
        this.representingItemStack.setItem(Items.idToItem.get(ByteBufPacketDecoder.readString(buffer)));
        this.representingItemStack.setAmount(buffer.readByte());
    }

    @Override
    public void save(WrappedJsonObject json) {
        super.save(json);
        WrappedJsonObject itemObject = new WrappedJsonObject();
        this.representingItemStack.saveAsJson(itemObject);
        json.put("item", itemObject);
    }

    @Override
    public void load(WrappedJsonObject json) {
        super.load(json);
        this.representingItemStack = ItemStack.readFromJson(json.getObject("item"));
    }
}
