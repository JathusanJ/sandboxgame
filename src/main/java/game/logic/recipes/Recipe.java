package game.logic.recipes;

import game.logic.world.items.Item;

import java.util.List;

public class Recipe {
    public int rows;
    public int columns;
    public List<Item> recipeInput;
    public Item recipeOutput;

    public Recipe(List<Item> recipeInput, Item recipeOutput, int rows, int columns) {
        this.recipeInput = recipeInput;
        this.recipeOutput = recipeOutput;
        this.rows = rows;
        this.columns = columns;
    }

    public boolean matches(List<Item> input, int rows, int columns) {
        if(this.rows != rows && this.columns != columns) return false;

        for (int i = 0; i < input.size(); i++) {
            if(input.get(i) != this.recipeInput.get(i)) return false;
        }

        return true;
    }

    public static String toRecipeInputString(List<Item> input) {
        String recipeInputString = "";

        for(Item item : input) {
            if(!recipeInputString.isEmpty()) {
                recipeInputString = recipeInputString + ";";
            }

            recipeInputString = recipeInputString + item.id;
        }

        return recipeInputString;
    }
}
