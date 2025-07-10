package game.logic.world.blocks.block_entity;

import game.logic.util.json.WrappedJsonList;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.items.BlockItem;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;

import java.util.ArrayList;

public class BarrelBlockEntity extends BlockEntity {
    public ItemStack[] contents = new ItemStack[4 * 9];

    public BarrelBlockEntity() {
        for (int i = 0; i < 27; i++) {
            contents[i] = new ItemStack(Items.AIR);
            contents[i].setAmount(0);
        }
    }

    @Override
    public void tick() {

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
