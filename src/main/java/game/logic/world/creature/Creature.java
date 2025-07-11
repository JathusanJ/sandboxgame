package game.logic.world.creature;

import game.client.ui.text.Language;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.World;
import game.logic.world.ServerWorld;
import game.networking.packets.CreatureMovePacket;
import io.netty.buffer.ByteBuf;
import org.joml.Vector2i;
import org.joml.Vector3f;

public abstract class Creature {
    public Vector3f position = new Vector3f();
    public Vector3f lastPosition = new Vector3f();
    public Vector3f size = new Vector3f();
    public Vector3f velocity = new Vector3f();
    public float pitch = 0F;
    public float yaw = 0F;
    public float lastPitch = 0F;
    public float lastYaw = 0F;
    public float health = 20F;
    public float maxHealth = 20F;
    public World world;
    public boolean isOnGround = true;
    public boolean markedForRemoval = false;
    public int networkId;

    public Vector2i getChunkPosition() {
        return new Vector2i((int) Math.floor(this.position.x / 16D), (int) Math.floor(this.position.z / 16D));
    }

    public void damage(float amount) {
        this.damage(amount, DamageSource.NONE);
    }

    public void damage(float amount, DamageSource damageSource) {
        if(amount < 0F) return;

        this.health = this.health - amount;
        if(this.health <= 0F) {
            this.kill(damageSource);
        }
    }

    public void heal(float amount) {
        this.health = Math.clamp(this.health + amount, 0, this.maxHealth);
    }

    public void kill() {
        this.kill(DamageSource.NONE);
    }

    public void kill(DamageSource damageSource) {
        this.health = 0F;
        this.onDeath(damageSource);
        this.remove();
    }

    public void onDeath(DamageSource damageSource) {

    }

    public abstract void tick();

    public void remove() {
        this.markedForRemoval = true;
    }

    public void checkAndHandleCollision(boolean yOnly) {
        Vector3f lowerCorner = new Vector3f(
                this.position.x - (this.size.x / 2F),
                this.position.y,
                this.position.z - (this.size.z / 2F)
        );
        Vector3f upperCorner = new Vector3f(
                this.position.x + (this.size.x / 2F),
                this.position.y + this.size.y,
                this.position.z + (this.size.z / 2F)
        );

        for (int y = (int) Math.floor(lowerCorner.y) - 1; y <= (int) Math.floor(upperCorner.y) + 1; y++) {
            for (int x = (int) Math.floor(lowerCorner.x) - 1; x <= (int) Math.floor(upperCorner.x) + 1; x++) {
                for (int z = (int) Math.floor(lowerCorner.z) - 1; z <= (int) Math.floor(upperCorner.z) + 1; z++) {
                    if(this.world.getBlockAt(x,y,z).hasCollision()) {
                        // Is it still colliding?
                        Box creatureBox = new Box(this.position.add(0, this.size.y / 2F, 0, new Vector3f()), this.size);
                        Box blockBox = new Box(new Vector3f(x + 0.5F,y + 0.5F,z + 0.5F), new Vector3f(1F,1F,1F));

                        if(!creatureBox.intersects(blockBox)) continue;

                        // https://blog.hamaluik.ca/posts/simple-aabb-collision-using-minkowski-difference/
                        Box minkowski = creatureBox.minkowski(blockBox);

                        float distance = Math.abs(minkowski.min().x);
                        Vector3f boundsPoint = new Vector3f(
                                minkowski.min().x, 0, 0
                        );

                        if(Math.abs(minkowski.max().x) < distance) {
                            distance = Math.abs(minkowski.max().x);
                            boundsPoint.x = minkowski.max().x;
                            boundsPoint.y = 0;
                            boundsPoint.z = 0;
                        }

                        if(Math.abs(minkowski.min().y) < distance) {
                            distance = Math.abs(minkowski.min().y);
                            boundsPoint.x = 0;
                            boundsPoint.y = minkowski.min().y;
                            boundsPoint.z = 0;
                        }

                        if(Math.abs(minkowski.max().y) < distance) {
                            distance = Math.abs(minkowski.max().y);
                            boundsPoint.x = 0;
                            boundsPoint.y = minkowski.max().y;
                            boundsPoint.z = 0;
                        }

                        if(Math.abs(minkowski.min().z) < distance) {
                            distance = Math.abs(minkowski.min().z);
                            boundsPoint.x = 0;
                            boundsPoint.y = 0;
                            boundsPoint.z = minkowski.min().z;
                        }

                        if(Math.abs(minkowski.max().z) < distance) {
                            distance = Math.abs(minkowski.max().z);
                            boundsPoint.x = 0;
                            boundsPoint.y = 0;
                            boundsPoint.z = minkowski.max().z;
                        }

                        if(boundsPoint.x != 0) {
                            this.velocity.x = 0;
                        }
                        if(boundsPoint.y != 0 && yOnly) {
                            if(distance == Math.abs(minkowski.min().y)) {
                                this.isOnGround = true;

                                // https://www.johannes-strommer.com/formeln/weg-geschwindigkeit-beschleunigung-zeit/
                                float fallDistance = (float) (Math.pow(this.velocity.y, 2) / (2 * 9.81 * 2));
                                this.damage((fallDistance - 3) * 0.5F, DamageSource.FALL_DAMAGE);
                            }
                            this.velocity.y = 0;
                        }
                        if(boundsPoint.z != 0) {
                            this.velocity.z = 0;
                        }

                        this.position.sub(boundsPoint);
                    }
                }
            }
        }
    }

    public void movementTick() {
        this.lastPosition.set(this.position);
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;

        this.velocity.y = (float) (this.velocity.y - 9.81 * 2 * 0.05F);

        this.velocity.x = this.velocity.x * 0.9F;
        if(Math.abs(this.velocity.x) < 0.01F) {
            this.velocity.x = 0F;
        }

        this.velocity.z = this.velocity.z * 0.95F;
        if(Math.abs(this.velocity.z) < 0.01F) {
            this.velocity.z = 0F;
        }

        this.position.add(this.velocity.x * 0.05F, 0F, this.velocity.z * 0.05F);
        this.checkAndHandleCollision(false);
        this.position.add(0F, this.velocity.y * 0.05F, 0F);
        this.checkAndHandleCollision(true);

        if(this.world instanceof ServerWorld serverWorld && serverWorld.worldTime % 20 == 0) {
            CreatureMovePacket packet = new CreatureMovePacket(this);
            serverWorld.server.sendPacketToAll(packet);
        }
    }

    public void writeSpawnPacket(ByteBuf buffer) {
        buffer.writeFloat(this.position.x);
        buffer.writeFloat(this.position.y);
        buffer.writeFloat(this.position.z);
        buffer.writeFloat(this.yaw);
    }

    public void readSpawnPacket(ByteBuf buffer) {
        this.setPosition(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
        this.yaw = buffer.readFloat();
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
        this.lastPosition.set(x, y, z);
    }

    public boolean isDead() {
        return this.health <= 0F;
    }

    public record Box(Vector3f center, Vector3f size) {
        public boolean intersects(Box otherBox) {
            Box minkowski = this.minkowski(otherBox);

            return minkowski.min().x <= 0 && minkowski.max().x >= 0 &&
                    minkowski.min().y <= 0 && minkowski.max().y >= 0 &&
                    minkowski.min().z <= 0 && minkowski.max().z >= 0;
        }

        public Vector3f min() {
            return new Vector3f(
                    this.center.x - this.size.x / 2,
                    this.center.y - this.size.y / 2,
                    this.center.z - this.size.z / 2
            );
        }

        public Vector3f max() {
            return new Vector3f(
                    this.center.x + this.size.x / 2,
                    this.center.y + this.size.y / 2,
                    this.center.z + this.size.z / 2
            );
        }

        public Box minkowski(Box otherBox) {
            Vector3f addedSize = this.size.add(otherBox.size, new Vector3f());
            return new Box(this.min().sub(otherBox.max()).add(addedSize.mul(0.5F, new Vector3f())), addedSize);
        }
    }

    public void save(WrappedJsonObject json) {
        WrappedJsonObject position = new WrappedJsonObject();
        position.put("x", this.position.x);
        position.put("y", this.position.y);
        position.put("z", this.position.z);
        json.put("position", position);

        WrappedJsonObject velocity = new WrappedJsonObject();
        velocity.put("x", this.velocity.x);
        velocity.put("y", this.velocity.y);
        velocity.put("z", this.velocity.z);
        json.put("velocity", velocity);

        json.put("health", this.health);
        json.put("maxHealth", this.health);
    }

    public void load(WrappedJsonObject json) {
        WrappedJsonObject position = json.getObject("position");
        this.position.set(position.getFloat("x"), position.getFloat("y"), position.getFloat("z"));
        this.lastPosition.set(this.position);

        WrappedJsonObject velocity = json.getObject("velocity");
        this.velocity.set(velocity.getFloat("x"), velocity.getFloat("y"), velocity.getFloat("z"));

        this.health = json.getFloat("health");
        this.maxHealth = json.getFloat("maxHealth");
    }

    public enum DamageSource {
        NONE("none"),
        FIRE("fire"),
        FALL_DAMAGE("fall_damage");

        String id;

        DamageSource(String id) {
            this.id = id;
        }

        public String getTranslationKey() {
            return "damage_source." + id;
        }

        public String getTranslated(String playerName) {
            return String.format(Language.translate(this.getTranslationKey()), playerName);
        }
    }
}
