package game.shared.world.items;

import game.shared.world.blocks.Block;
import game.shared.world.blocks.Blocks;

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
    public static BlockItem IRON_ORE = registerBlockItem(Blocks.IRON_ORE);

    public static BlockItem RAW_IRON_CHUNK_BLOCK = registerBlockItem(Blocks.RAW_IRON_CHUNK_BLOCK);
    public static BlockItem IRON_BLOCK = registerBlockItem(Blocks.IRON_BLOCK);

    public static BlockItem OAK_LOG = registerBlockItem(Blocks.OAK_LOG);
    public static BlockItem OAK_PLANKS = registerBlockItem(Blocks.OAK_PLANKS);
    public static BlockItem OAK_LEAVES = registerBlockItem(Blocks.OAK_LEAVES);

    public static BlockItem BIRCH_LOG = registerBlockItem(Blocks.BIRCH_LOG);
    public static BlockItem BIRCH_PLANKS = registerBlockItem(Blocks.BIRCH_PLANKS);
    public static BlockItem BIRCH_LEAVES = registerBlockItem(Blocks.BIRCH_LEAVES);

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

    public static BlockItem RED_TULIP = registerBlockItem(Blocks.RED_TULIP, "red_tulip");
    public static BlockItem ORANGE_TULIP = registerBlockItem(Blocks.ORANGE_TULIP, "orange_tulip");
    public static BlockItem YELLOW_TULIP = registerBlockItem(Blocks.YELLOW_TULIP, "yellow_tulip");
    public static BlockItem SHORT_GRASS = registerBlockItem(Blocks.SHORT_GRASS, "short_grass");

    public static BlockItem PUMPKIN = registerBlockItem(Blocks.PUMPKIN);
    public static BlockItem CARVED_PUMPKIN = registerBlockItem(Blocks.CARVED_PUMPKIN);

    public static Item COAL = register(new Item(), "coal");
    public static Item RAW_IRON_CHUNK = register(new Item(), "raw_iron_chunk");
    public static Item IRON_INGOT = register(new Item(), "iron_ingot");
    public static Item STICK = register(new Item(), "stick");
    public static Item WOODEN_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.WOODEN), "wooden_pickaxe");
    public static Item STONE_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.STONE), "stone_pickaxe");
    public static Item IRON_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.IRON), "iron_pickaxe");
    public static Item DIAMOND_PICKAXE = register(new PickaxeItem(PickaxeItem.PickaxeTier.DIAMOND), "diamond_pickaxe");

    public static Item KNIFE = register(new KnifeItem(), "knife");

    public static BlockItem registerBlockItem(Block block, String itemTextureOvveride) {
        BlockItem asItem = (BlockItem) register(new BlockItem(block, itemTextureOvveride), block.getBlockId());
        block.asItem = asItem;
        return asItem;
    }

    public static BlockItem registerBlockItem(Block block) {
        return registerBlockItem(block, null);
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
                IRON_ORE,
                RAW_IRON_CHUNK_BLOCK,
                IRON_BLOCK,
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

                BIRCH_LOG,
                BIRCH_PLANKS,
                BIRCH_LEAVES,

                BARREL,

                RED_TULIP,
                ORANGE_TULIP,
                YELLOW_TULIP,
                SHORT_GRASS,

                PUMPKIN,
                CARVED_PUMPKIN,

                COAL,
                RAW_IRON_CHUNK,
                IRON_INGOT,
                STICK,

                WOODEN_PICKAXE,
                STONE_PICKAXE,
                IRON_PICKAXE
        );
    }
}
