package game.logic.world.items;

import game.logic.world.blocks.Block;
import game.logic.world.blocks.Blocks;

import java.util.HashMap;
import java.util.List;

public class Items {
    public static HashMap<String, Item> idToItem = new HashMap<>();

    public static BlockItem MOONDUST = registerBlockItem(Blocks.MOONDUST);
    public static BlockItem AIR = registerBlockItem(Blocks.AIR);
    public static BlockItem BEDROCK = registerBlockItem(Blocks.BEDROCK);
    public static BlockItem COBBLESTONE = registerBlockItem(Blocks.COBBLESTONE);
    public static BlockItem GRASS = registerBlockItem(Blocks.GRASS);
    public static BlockItem DIRT = registerBlockItem(Blocks.DIRT);

    public static BlockItem STONE = registerBlockItem(Blocks.STONE);
    public static BlockItem COAL_ORE = registerBlockItem(Blocks.COAL_ORE);

    public static BlockItem OAK_LOG = registerBlockItem(Blocks.OAK_LOG);
    public static BlockItem OAK_PLANKS = registerBlockItem(Blocks.OAK_PLANKS);
    public static BlockItem OAK_LEAVES = registerBlockItem(Blocks.OAK_LEAVES);

    public static BlockItem BIRCH_LOG = registerBlockItem(Blocks.BIRCH_LOG);
    public static BlockItem BIRCH_PLANKS = registerBlockItem(Blocks.BIRCH_PLANKS);

    public static BlockItem ACACIA_LOG = registerBlockItem(Blocks.ACACIA_LOG);
    public static BlockItem ACACIA_PLANKS = registerBlockItem(Blocks.ACACIA_PLANKS);

    public static BlockItem JUNGLE_LOG = registerBlockItem(Blocks.JUNGLE_LOG);
    public static BlockItem JUNGLE_PLANKS = registerBlockItem(Blocks.JUNGLE_PLANKS);

    public static BlockItem CRAFTING_TABLE = registerBlockItem(Blocks.CRAFTING_TABLE);
    public static BlockItem FURNACE = registerBlockItem(Blocks.FURNACE);

    public static BlockItem WATER = registerBlockItem(Blocks.WATER);
    public static BlockItem LAVA = registerBlockItem(Blocks.LAVA);

    public static BlockItem SAND = registerBlockItem(Blocks.SAND);

    public static BlockItem BARREL = registerBlockItem(Blocks.BARREL);

    public static Item COAL = register(new Item(), "coal");
    public static Item STICK = register(new Item(), "stick");
    public static Item WOODEN_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.WOODEN), "wooden_pickaxe");
    public static Item STONE_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.STONE), "stone_pickaxe");
    public static Item IRON_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.IRON), "iron_pickaxe");
    public static Item DIAMOND_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.DIAMOND), "diamond_pickaxe");

    public static BlockItem registerBlockItem(Block block) {
        BlockItem asItem = (BlockItem) register(new BlockItem(block), block.getBlockId());
        block.asItem = asItem;
        return asItem;
    }

    public static Item register(Item item, String id) {
        item.setItemId(id);
        idToItem.put(id, item);

        return item;
    }

    public static void init() {

    }

    public static List<Item> sorted() {
        return List.of(
                GRASS,
                DIRT,
                STONE,
                COAL_ORE,
                COBBLESTONE,
                BEDROCK,
                WATER,
                LAVA,
                SAND,

                CRAFTING_TABLE,
                FURNACE,

                OAK_LOG,
                OAK_PLANKS,
                OAK_LEAVES,

                BARREL,

                COAL,
                STICK,

                WOODEN_PICKAXE
        );
    }
}
