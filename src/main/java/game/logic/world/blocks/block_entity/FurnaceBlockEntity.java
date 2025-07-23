package game.logic.world.blocks.block_entity;

import game.logic.recipes.FurnaceRecipes;
import game.logic.recipes.Recipe;
import game.logic.util.json.WrappedJsonObject;
import game.logic.world.World;
import game.logic.world.blocks.FurnaceBlock;
import game.logic.world.items.Item;
import game.logic.world.items.ItemStack;
import game.logic.world.items.Items;
import game.logic.world.items.slot.ContainerItemSlot;
import game.logic.world.items.slot.RegularItemSlot;
import org.joml.Vector3i;

import java.util.HashMap;

public class FurnaceBlockEntity extends BlockEntity {
    public int remainingFuelTime = 0;
    public int burnTime = 0;
    public Recipe currentRecipe;
    public RegularItemSlot inputSlot = new ContainerItemSlot(this);
    public RegularItemSlot fuelSlot = new ContainerItemSlot(this);
    public RegularItemSlot outputSlot = new ContainerItemSlot(this);

    public static HashMap<Item, Integer> itemFuelTimes = new HashMap<>();

    public FurnaceBlockEntity(World world, int x, int y, int z) {
        this.world = world;
        this.position = new Vector3i(x,y,z);
    }

    @Override
    public void tick() {
        if(currentRecipe == null && !inputSlot.isEmpty()) {
            currentRecipe = FurnaceRecipes.findRecipe(inputSlot.getItem());
        }

        if(currentRecipe != null && !inputSlot.isEmpty() && currentRecipe.recipeInput.contains(inputSlot.getItem()) && (outputSlot.isEmpty() || currentRecipe.recipeOutput == outputSlot.getItem())) {
            if(remainingFuelTime == 0 && !fuelSlot.isEmpty() && itemFuelTimes.containsKey(fuelSlot.getItem())) {
                fuelSlot.setAmount(fuelSlot.getAmount() - 1);
                remainingFuelTime = remainingFuelTime + itemFuelTimes.get(fuelSlot.getItem());
                this.needsSaving();
            }

            if(remainingFuelTime > 0) {
                burnTime = burnTime + 1;
                if (burnTime >= 100) {
                    burnTime = 0;
                    outputSlot.setItem(currentRecipe.recipeOutput);
                    outputSlot.setAmount(outputSlot.getAmount() + 1);
                    inputSlot.setAmount(inputSlot.getAmount() - 1);
                }
                this.needsSaving();
            }
        } else {
            currentRecipe = null;
            burnTime = 0;
        }

        if(remainingFuelTime > 0) {
            remainingFuelTime = remainingFuelTime - 1;
            this.needsSaving();
        } else {
            burnTime = 0;
        }
    }

    @Override
    public void save(WrappedJsonObject json) {
        WrappedJsonObject input = new WrappedJsonObject();
        WrappedJsonObject output = new WrappedJsonObject();
        WrappedJsonObject fuel = new WrappedJsonObject();

        this.inputSlot.getItemStack().saveAsJson(input);
        this.outputSlot.getItemStack().saveAsJson(output);
        this.fuelSlot.getItemStack().saveAsJson(fuel);

        json.put("input", input);
        json.put("output", output);
        json.put("fuel", fuel);

        json.put("burnTime", this.burnTime);
        json.put("remainingFuelTime", this.remainingFuelTime);
    }

    @Override
    public void load(WrappedJsonObject json) {
        this.inputSlot.setItemStack(ItemStack.readFromJson(json.getObject("input")));
        this.outputSlot.setItemStack(ItemStack.readFromJson(json.getObject("output")));
        this.fuelSlot.setItemStack(ItemStack.readFromJson(json.getObject("fuel")));
        this.burnTime = json.getInt("burnTime");
        this.remainingFuelTime = json.getInt("remainingFuelTime");
    }

    public static void loadFuelTimes() {
        itemFuelTimes.put(Items.OAK_PLANKS, 25);
        itemFuelTimes.put(Items.BIRCH_PLANKS, 25);
        itemFuelTimes.put(Items.ACACIA_PLANKS, 25);
        itemFuelTimes.put(Items.JUNGLE_PLANKS, 25);
        itemFuelTimes.put(Items.OAK_LOG, 100);
        itemFuelTimes.put(Items.BIRCH_LOG, 100);
        itemFuelTimes.put(Items.ACACIA_LOG, 100);
        itemFuelTimes.put(Items.JUNGLE_LOG, 100);
        itemFuelTimes.put(Items.COAL, 800);
        itemFuelTimes.put(Items.STICK, 10);
    }
}
