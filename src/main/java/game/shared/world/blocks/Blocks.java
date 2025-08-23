package game.shared.world.blocks;

import java.util.HashMap;

public class Blocks {
    public static HashMap<String, Block> idToBlock = new HashMap<>();

    public static Block MOONDUST = register(new Block(), "moondust");
    public static AirBlock AIR = (AirBlock) register(new AirBlock(), "air");
    public static BedrockBlock BEDROCK = (BedrockBlock) register(new BedrockBlock(), "bedrock");
    public static CobblestoneBlock COBBLESTONE = (CobblestoneBlock) register(new CobblestoneBlock(), "cobblestone");
    public static GrassBlock GRASS = (GrassBlock) register(new GrassBlock(), "grass");
    public static DirtBlock DIRT = (DirtBlock) register(new DirtBlock(), "dirt");

    public static StoneBlock STONE = (StoneBlock) register(new StoneBlock(), "stone");
    public static CoalOreBlock COAL_ORE = (CoalOreBlock) register(new CoalOreBlock(), "coal_ore");
    public static IronOreBlock IRON_ORE = (IronOreBlock) register(new IronOreBlock(), "iron_ore");

    public static IronBlock IRON_BLOCK = (IronBlock) register(new IronBlock(), "iron_block");
    public static RawIronChunkBlock RAW_IRON_CHUNK_BLOCK = (RawIronChunkBlock) register(new RawIronChunkBlock(), "raw_iron_chunk_block");

    public static OakPlanksBlock OAK_PLANKS = (OakPlanksBlock) register(new OakPlanksBlock(), "oak_planks");
    public static OakLogBlock OAK_LOG = (OakLogBlock) register(new OakLogBlock(), "oak_log");
    public static OakLeavesBlock OAK_LEAVES = (OakLeavesBlock) register(new OakLeavesBlock(), "oak_leaves");

    public static BirchPlanksBlock BIRCH_PLANKS = (BirchPlanksBlock) register(new BirchPlanksBlock(), "birch_planks");
    public static BirchLogBlock BIRCH_LOG = (BirchLogBlock) register(new BirchLogBlock(), "birch_log");

    public static AcaciaPlanksBlock ACACIA_PLANKS = (AcaciaPlanksBlock) register(new AcaciaPlanksBlock(), "acacia_planks");
    public static AcaciaLogBlock ACACIA_LOG = (AcaciaLogBlock) register(new AcaciaLogBlock(), "acacia_log");

    public static JunglePlanksBlock JUNGLE_PLANKS = (JunglePlanksBlock) register(new JunglePlanksBlock(), "jungle_planks");
    public static JungleLogBlock JUNGLE_LOG = (JungleLogBlock) register(new JungleLogBlock(), "jungle_log");

    public static CraftingTableBlock CRAFTING_TABLE = (CraftingTableBlock) register(new CraftingTableBlock(), "crafting_table");
    public static FurnaceBlock FURNACE = (FurnaceBlock) register(new FurnaceBlock(), "furnace");

    public static WaterBlock WATER = (WaterBlock) register(new WaterBlock(), "water");
    public static LavaBlock LAVA = (LavaBlock) register(new LavaBlock(), "lava");

    public static SandBlock SAND = (SandBlock) register(new SandBlock(), "sand");

    public static BarrelBlock BARREL = (BarrelBlock) register(new BarrelBlock(), "barrel");

    public static RedTulipBlock RED_TULIP = (RedTulipBlock) register(new RedTulipBlock(), "red_tulip");
    public static OrangeTulipBlock ORANGE_TULIP = (OrangeTulipBlock) register(new OrangeTulipBlock(), "orange_tulip");
    public static YellowTulipBlock YELLOW_TULIP = (YellowTulipBlock) register(new YellowTulipBlock(), "yellow_tulip");
    public static ShortGrassBlock SHORT_GRASS = (ShortGrassBlock) register(new ShortGrassBlock(), "short_grass");

    public static BarrierBlock BARRIER = (BarrierBlock) register(new BarrierBlock(), "barrier");

    public static Block register(Block block, String id) {
        block.setBlockId(id);
        idToBlock.put(id, block);

        return block;
    }

    public static void init() {

    }
}
