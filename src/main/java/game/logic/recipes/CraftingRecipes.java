package game.logic.recipes;

import game.logic.world.items.Item;
import game.logic.world.items.Items;

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

        // Remove any space to the left of the input
        for(int x = 0; x < totalColumns; x++) {
            boolean columnEmpty = true;
            for(int y = 0; y < totalRows; y++) {
                if(input.get(y * totalColumns + x) != Items.AIR) {
                    columnEmpty = false;
                    break;
                }
            }

            if(columnEmpty) {
                ArrayList<Item> newInput = new ArrayList<>();

                for(int x2 = 1; x2 < totalColumns; x2++) {
                    for(int y = 0; y < totalRows; y++) {
                       newInput.add(input.get(y * totalColumns + x2));
                    }
                }

                totalColumns = totalColumns - 1;
                x = x - 1;

                input = newInput;
            } else {
                break;
            }
        }

        // Clear out space to the right of the input
        for(int x = totalColumns - 1; x > 0; x--) {
            boolean columnEmpty = true;
            for(int y = 0; y < totalRows; y++) {
                if(input.get(y * totalColumns + x) != Items.AIR) {
                    columnEmpty = false;
                    break;
                }
            }

            if(columnEmpty) {
                ArrayList<Item> newInput = new ArrayList<>();
                for(int x2 = 0; x2 < totalColumns - 1; x2++) {
                    for(int y = 0; y < totalRows; y++) {
                        newInput.add(input.get(y * totalColumns + x2));
                    }
                }

                input = newInput;
                totalColumns = totalColumns - 1;
            } else {
                break;
            }
        }

        String recipeInputString = Recipe.toRecipeInputString(input);

        return recipes.get(recipeInputString);
    }

    public static Recipe createRecipe(List<Item> input, Item output, int rows, int columns) {
        String recipeInputString = Recipe.toRecipeInputString(input);

        Recipe recipe = new Recipe(input, output, rows, columns);
        recipes.put(recipeInputString, recipe);

        return recipe;
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

        // Barrel (oak planks)
        createRecipe(
                List.of(
                        Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS,
                        Items.OAK_PLANKS, Items.AIR,        Items.OAK_PLANKS,
                        Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS
                ),
                Items.BARREL,
                3,
                3
        );

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
                1
        );

        // Oak Planks
        createRecipe(
                List.of(
                        Items.OAK_LOG
                ),
                Items.OAK_PLANKS,
                1,
                1
        );

        // Birch Planks
        createRecipe(
                List.of(
                        Items.BIRCH_LOG
                ),
                Items.BIRCH_PLANKS,
                1,
                1
        );

        // Acacia Planks
        createRecipe(
                List.of(
                        Items.ACACIA_LOG
                ),
                Items.ACACIA_PLANKS,
                1,
                1
        );

        // Jungle Planks
        createRecipe(
                List.of(
                        Items.JUNGLE_LOG
                ),
                Items.JUNGLE_PLANKS,
                1,
                1
        );
    }
}
