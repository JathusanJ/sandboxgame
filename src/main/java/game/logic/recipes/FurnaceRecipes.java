package game.logic.recipes;

import game.logic.world.items.Item;
import game.logic.world.items.Items;

import java.util.HashMap;
import java.util.List;

public class FurnaceRecipes {
    public static HashMap<String, Recipe> recipes = new HashMap<>();

    public static Recipe findRecipe(Item input) {
        String recipeInputString = Recipe.toRecipeInputString(List.of(input), 1, 1);

        return recipes.get(recipeInputString);
    }

    public static Recipe createRecipe(Item input, Item output) {
        String recipeInputString = Recipe.toRecipeInputString(List.of(input), 1, 1);

        Recipe recipe = new Recipe(List.of(input), output, 1, 1);
        recipes.put(recipeInputString, recipe);

        return recipe;
    }

    public static void initialize() {
        createRecipe(Items.COBBLESTONE, Items.STONE);
        createRecipe(Items.RAW_IRON_CHUNK, Items.IRON_INGOT);
        createRecipe(Items.IRON_ORE, Items.IRON_INGOT);
        createRecipe(Items.COAL_ORE, Items.COAL);
    }
}
