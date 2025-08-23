package game.shared.recipes;

import game.shared.world.items.Item;
import game.shared.world.items.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraftingRecipes {
    public static HashMap<String, Recipe> recipes = new HashMap<>();

    public static Recipe findRecipe(ArrayList<Item> input, int totalRows, int totalColumns) {
        // Clear out space above the input
        for(int y = 0; y < totalRows; y++) {
            boolean rowEmpty = true;
            for (int x = 0; x < totalColumns; x++) {
                if(input.get(y * totalColumns + x) != Items.AIR) {
                    rowEmpty = false;
                    break;
                }
            }

            if(rowEmpty) {
                totalRows = totalRows - 1;
                y = y - 1;

                for(int i = 0; i < totalColumns; i++) {
                    input.removeFirst();
                }
            } else {
                break;
            }
        }

        // If no input was given (so the input list was originally just air) the input list should now be empty
        if(input.isEmpty()) return null;

        // Clear out space below the input
        for (int y = totalRows - 1; y > 0; y--) {
            boolean rowEmpty = true;
            for(int x = 0; x < totalColumns; x++) {
                if(input.get(y * totalColumns + x) != Items.AIR) {
                    rowEmpty = false;
                    break;
                }
            }

            if(rowEmpty) {
                // Reduce totalRows by one and remove the air blocks at the end
                totalRows = totalRows - 1;
                for(int i = 0; i < totalColumns; i++) {
                    input.removeLast();
                }
            } else {
                break;
            }
        }

        // I couldn't get the column stuff working with what I had originally, so I'm just gonna go for a very very very comprehensible solution instead

        ArrayList<ArrayList<Item>> itemGrid = new ArrayList<>();

        for (int i = 0; i < totalRows; i++) {
            ArrayList<Item> row = new ArrayList<>();

            for (int column = 0; column < totalColumns; column++) {
                row.add(input.get(i * totalColumns + column));
            }

            itemGrid.add(row);
        }

        // Remove any space to the left of the input
        for(int x = 0; x < totalColumns; x++) {
            boolean columnEmpty = true;

            for(ArrayList<Item> row : itemGrid) {
                if(row.getFirst() != Items.AIR) {
                    columnEmpty = false;
                    break;
                }
            }

            if(columnEmpty) {
                for(ArrayList<Item> row : itemGrid) {
                    row.removeFirst();
                }
            } else {
                break;
            }
        }

        // Clear out space to the right of the input
        for(int column = 0; column < totalColumns; column++) {
            boolean columnEmpty = true;

            for(ArrayList<Item> row : itemGrid) {
                if(row.getLast() != Items.AIR) {
                    columnEmpty = false;
                    break;
                }
            }

            if(columnEmpty) {
                for(ArrayList<Item> row : itemGrid) {
                    row.removeLast();
                }
            } else {
                break;
            }
        }

        input.clear();

        totalColumns = itemGrid.getFirst().size();
        totalRows = itemGrid.size();
        for(ArrayList<Item> row : itemGrid) {
            for(Item item : row) {
                input.add(item);
            }
        }

        String recipeInputString = Recipe.toRecipeInputString(input, totalRows, totalColumns);
        return recipes.get(recipeInputString);
    }

    public static Recipe createRecipe(List<Item> input, Item output, int rows, int columns, int amount) {
        String recipeInputString = Recipe.toRecipeInputString(input, rows, columns);

        Recipe recipe = new Recipe(input, output, rows, columns, amount);
        recipes.put(recipeInputString, recipe);

        return recipe;
    }

    public static Recipe createRecipe(List<Item> input, Item output, int rows, int columns) {
        return createRecipe(input, output, rows, columns, 1);
    }

    public static void initialize() {
        // Crafting table (oak planks)
        createRecipe(
                List.of(
                        Items.OAK_PLANKS, Items.OAK_PLANKS,
                        Items.OAK_PLANKS, Items.OAK_PLANKS
                ),
                Items.CRAFTING_TABLE,
                2,
                2
        );

        // Barrel
        createBarrelRecipe(Items.OAK_PLANKS);
        createBarrelRecipe(Items.BIRCH_PLANKS);
        createBarrelRecipe(Items.ACACIA_PLANKS);
        createBarrelRecipe(Items.JUNGLE_PLANKS);

        // Furnace
        createRecipe(
                List.of(
                        Items.COBBLESTONE, Items.COBBLESTONE, Items.COBBLESTONE,
                        Items.COBBLESTONE, Items.AIR,         Items.COBBLESTONE,
                        Items.COBBLESTONE, Items.COBBLESTONE, Items.COBBLESTONE
                ),
                Items.FURNACE,
                3,
                3
        );

        // Sticks
        createRecipe(
                List.of(
                        Items.OAK_PLANKS,
                        Items.OAK_PLANKS
                ),
                Items.STICK,
                2,
                1,
                4
        );

        // Oak Planks
        createRecipe(
                List.of(
                        Items.OAK_LOG
                ),
                Items.OAK_PLANKS,
                1,
                1,
                4
        );

        // Birch Planks
        createRecipe(
                List.of(
                        Items.BIRCH_LOG
                ),
                Items.BIRCH_PLANKS,
                1,
                1,
                4
        );

        // Acacia Planks
        createRecipe(
                List.of(
                        Items.ACACIA_LOG
                ),
                Items.ACACIA_PLANKS,
                1,
                1,
                4
        );

        // Jungle Planks
        createRecipe(
                List.of(
                        Items.JUNGLE_LOG
                ),
                Items.JUNGLE_PLANKS,
                1,
                1,
                4
        );

        createPickaxeRecipe(Items.OAK_PLANKS, Items.WOODEN_PICKAXE);
        createPickaxeRecipe(Items.BIRCH_PLANKS, Items.WOODEN_PICKAXE);
        createPickaxeRecipe(Items.ACACIA_PLANKS, Items.WOODEN_PICKAXE);
        createPickaxeRecipe(Items.JUNGLE_PLANKS, Items.WOODEN_PICKAXE);
        createPickaxeRecipe(Items.COBBLESTONE, Items.STONE_PICKAXE);
        createPickaxeRecipe(Items.STONE, Items.STONE_PICKAXE);
        createPickaxeRecipe(Items.IRON_INGOT, Items.IRON_PICKAXE);

        createRecipe(
                List.of(
                        Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK,
                        Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK,
                        Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK, Items.RAW_IRON_CHUNK
                ),
                Items.RAW_IRON_CHUNK_BLOCK,
                3,
                3,
                1
        );

        createRecipe(
                List.of(
                        Items.RAW_IRON_CHUNK_BLOCK
                ),
                Items.RAW_IRON_CHUNK,
                1,
                1,
                9
        );

        createRecipe(
                List.of(
                        Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT,
                        Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT,
                        Items.IRON_INGOT, Items.IRON_INGOT, Items.IRON_INGOT
                ),
                Items.IRON_BLOCK,
                3,
                3,
                1
        );

        createRecipe(
                List.of(
                        Items.IRON_BLOCK
                ),
                Items.IRON_INGOT,
                1,
                1,
                9
        );
    }

    public static void createPickaxeRecipe(Item material, Item output) {
        createRecipe(
                List.of(
                        material, material, material,
                        Items.AIR, Items.STICK, Items.AIR,
                        Items.AIR, Items.STICK, Items.AIR
                ),
                output,
                3,
                3
        );
    }

    public static void createBarrelRecipe(Item material) {
        createRecipe(
                List.of(
                        material, material, material,
                        material, Items.AIR, material,
                        material, material, material
                ),
                Items.BARREL,
                3,
                3
        );
    }
}
