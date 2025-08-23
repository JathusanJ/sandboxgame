package game.shared.world.blocks.block_entity;

import game.shared.util.json.WrappedJsonList;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.World;
import game.shared.world.creature.ItemCreature;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import org.joml.Vector3i;

public class BarrelBlockEntity extends BlockEntity {
    public ItemStack[] contents = new ItemStack[4 * 9];

    public BarrelBlockEntity(World world, int x, int y, int z) {
        this.world = world;
        this.position = new Vector3i(x,y,z);

        for (int i = 0; i < 27; i++) {
            contents[i] = new ItemStack(Items.AIR);
            contents[i].setAmount(0);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void onDestroy() {
        for(int i = 0; i < 27; i++) {
            if(contents[i].getItem() != Items.AIR && contents[i].amount != 0) {
                ItemCreature itemCreature = new ItemCreature();
                itemCreature.representingItemStack = contents[i];
                itemCreature.setPosition(this.position.x, this.position.y + 0.5F, this.position.z);
                itemCreature.velocity.set(this.world.random.nextFloat() * 2F, Math.abs(this.world.random.nextFloat()) * 2F, this.world.random.nextFloat() * 2F);
                this.world.spawnCreature(itemCreature);
            }
        }
    }

    @Override
    public void save(WrappedJsonObject json) {
        WrappedJsonList data = new WrappedJsonList();
        for(int i = 0; i < 27; i++) {
            WrappedJsonObject itemData = new WrappedJsonObject();
            contents[i].saveAsJson(itemData);
            data.add(itemData);
        }
        json.put("items", data);
    }

    @Override
    public void load(WrappedJsonObject json) {
        WrappedJsonList items = json.getJsonList("items");
        for(int i = 0; i < 27; i++) {
            contents[i] = ItemStack.readFromJson(items.getObject(i));
        }
    }
}
