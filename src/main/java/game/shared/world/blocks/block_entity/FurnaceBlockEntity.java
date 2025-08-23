package game.shared.world.blocks.block_entity;

import game.shared.recipes.FurnaceRecipes;
import game.shared.recipes.Recipe;
import game.shared.util.json.WrappedJsonObject;
import game.shared.world.World;
import game.shared.world.creature.ItemCreature;
import game.shared.world.items.Item;
import game.shared.world.items.ItemStack;
import game.shared.world.items.Items;
import game.shared.world.items.slot.ContainerItemSlot;
import game.shared.world.items.slot.RegularItemSlot;
import org.joml.Vector3i;

import java.util.HashMap;

public class FurnaceBlockEntity extends BlockEntity {
    public int remainingFuelTime = 0;
    public int burnTime = 0;
    public Item lastFuelItem;
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
        if(this.currentRecipe == null && !this.inputSlot.isEmpty()) {
            this.currentRecipe = FurnaceRecipes.findRecipe(this.inputSlot.getItem());
        }

        if(currentRecipe != null && !inputSlot.isEmpty() && currentRecipe.recipeInput.contains(inputSlot.getItem()) && (outputSlot.isEmpty() || currentRecipe.recipeOutput == outputSlot.getItem())) {
            if(this.remainingFuelTime == 0 && !fuelSlot.isEmpty() && itemFuelTimes.containsKey(fuelSlot.getItem())) {
                this.lastFuelItem = fuelSlot.getItem();
                this.fuelSlot.setAmount(fuelSlot.getAmount() - 1);
                this.remainingFuelTime = this.remainingFuelTime + itemFuelTimes.get(this.fuelSlot.getItem());
                this.needsSaving();
            }

            if(this.remainingFuelTime > 0) {
                this.burnTime = this.burnTime + 1;
                if (this.burnTime >= 100) {
                    this.burnTime = 0;
                    this.outputSlot.setItem(this.currentRecipe.recipeOutput);
                    this.outputSlot.setAmount(this.outputSlot.getAmount() + 1);
                    this.inputSlot.setAmount(this.inputSlot.getAmount() - 1);
                }
                this.needsSaving();
            }
        } else {
            this.currentRecipe = null;
            this.burnTime = 0;
        }

        if(this.remainingFuelTime > 0) {
            this.remainingFuelTime = this.remainingFuelTime - 1;
            this.needsSaving();
        } else {
            this.burnTime = 0;
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

    @Override
    public void onDestroy() {
        if(!this.inputSlot.isEmpty()) {
            ItemCreature itemCreature = new ItemCreature();
            itemCreature.representingItemStack = this.inputSlot.getItemStack();
            itemCreature.setPosition(this.position.x, this.position.y + 0.5F, this.position.z);
            itemCreature.velocity.set(this.world.random.nextFloat() * 2F, Math.abs(this.world.random.nextFloat()) * 2F, this.world.random.nextFloat() * 2F);
            this.world.spawnCreature(itemCreature);
        }
        if(!this.outputSlot.isEmpty()) {
            ItemCreature itemCreature = new ItemCreature();
            itemCreature.representingItemStack = this.outputSlot.getItemStack();
            itemCreature.setPosition(this.position.x, this.position.y + 0.5F, this.position.z);
            itemCreature.velocity.set(this.world.random.nextFloat() * 2F, Math.abs(this.world.random.nextFloat()) * 2F, this.world.random.nextFloat() * 2F);
            this.world.spawnCreature(itemCreature);
        }
        if(!this.fuelSlot.isEmpty()) {
            ItemCreature itemCreature = new ItemCreature();
            itemCreature.representingItemStack = this.fuelSlot.getItemStack();
            itemCreature.setPosition(this.position.x, this.position.y + 0.5F, this.position.z);
            itemCreature.velocity.set(this.world.random.nextFloat() * 2F, Math.abs(this.world.random.nextFloat()) * 2F, this.world.random.nextFloat() * 2F);
            this.world.spawnCreature(itemCreature);
        }
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
        itemFuelTimes.put(Items.WOODEN_PICKAXE, 100);
    }
}
