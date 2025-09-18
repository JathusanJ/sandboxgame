package game.client.ui.text;

import engine.renderer.Texture;

import java.util.HashMap;

// TODO: Define the font stuff outside of code
public class Font {
    public static Texture FONT_TEXTURE = new Texture("textures/font.png");

    private static final HashMap<String, Float> characterSize = new HashMap<>();
    private static char[] fontIndex = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.?!$:=+-[]()_;\",<>/\\#*%&'äüö|     ".toCharArray();

    public static void initialize(){
        if(!characterSize.isEmpty()) return;
        characterSize.put(" ", 8F);
        characterSize.put("a", 9F);
        characterSize.put("b", 10F);
        characterSize.put("c", 8F);
        characterSize.put("d", 10F);
        characterSize.put("e", 10F);
        characterSize.put("f", 7F);
        characterSize.put("g", 10F);
        characterSize.put("h", 9F);
        characterSize.put("i", 2F);
        characterSize.put("j", 5F);
        characterSize.put("k", 9F);
        characterSize.put("l", 2F);
        characterSize.put("m", 16F);
        characterSize.put("n", 9F);
        characterSize.put("o", 11F);
        characterSize.put("p", 10F);
        characterSize.put("q", 10F);
        characterSize.put("r", 6F);
        characterSize.put("s", 7F);
        characterSize.put("t", 7F);
        characterSize.put("u", 9F);
        characterSize.put("v", 10F);
        characterSize.put("w", 16F);
        characterSize.put("x", 10F);
        characterSize.put("y", 11F);
        characterSize.put("z", 7F);

        characterSize.put("A", 14F);
        characterSize.put("B", 10F);
        characterSize.put("C", 11F);
        characterSize.put("D", 12F);
        characterSize.put("E", 8F);
        characterSize.put("F", 8F);
        characterSize.put("G", 12F);
        characterSize.put("H", 12F);
        characterSize.put("I", 2F);
        characterSize.put("J", 7F);
        characterSize.put("K", 11F);
        characterSize.put("L", 8F);
        characterSize.put("M", 15F);
        characterSize.put("N", 12F);
        characterSize.put("O", 14F);
        characterSize.put("P", 10F);
        characterSize.put("Q", 15F);
        characterSize.put("R", 11F);
        characterSize.put("S", 10F);
        characterSize.put("T", 12F);
        characterSize.put("U", 11F);
        characterSize.put("V", 13F);
        characterSize.put("W", 16F);
        characterSize.put("X", 13F);
        characterSize.put("Y", 12F);
        characterSize.put("Z", 12F);

        characterSize.put("0", 10F);
        characterSize.put("1", 5F);
        characterSize.put("2", 9F);
        characterSize.put("3", 9F);
        characterSize.put("4", 11F);
        characterSize.put("5", 8F);
        characterSize.put("6", 10F);
        characterSize.put("7", 10F);
        characterSize.put("8", 10F);
        characterSize.put("9", 10F);
        characterSize.put(".", 3F);
        characterSize.put("?", 9F);
        characterSize.put("!", 3F);
        characterSize.put("$", 8F);
        characterSize.put(":", 3F);
        characterSize.put("=", 10F);
        characterSize.put("+", 10F);
        characterSize.put("-", 8F);
        characterSize.put("[", 4F);
        characterSize.put("]", 4F);
        characterSize.put("(", 4F);
        characterSize.put(")", 4F);
        characterSize.put("_", 10F);
        characterSize.put(";", 3F);
        characterSize.put("\"", 5F);
        characterSize.put(",", 3F);
        characterSize.put("<", 9F);
        characterSize.put(">", 9F);
        characterSize.put("/", 9F);
        characterSize.put("\\", 9F);
        characterSize.put("#", 11F);
        characterSize.put("*", 7F);
        characterSize.put("%", 15F);
        characterSize.put("&", 13F);
        characterSize.put("'", 2F);
        characterSize.put("ä", 9F);
        characterSize.put("ü", 9F);
        characterSize.put("ö", 11F);
        characterSize.put("|", 4F);
    }

    public static float getCharacterWidth(String character){
        return getCharacterWidth(character, 16F);
    }

    public static float getCharacterWidth(String character, float fontSize){
        return characterSize.getOrDefault(character, fontSize);
    }

    public static float getTextWidth(String text){
        return getTextWidth(text, 16);
    }

    public static float getTextWidth(String text, float fontSize){
        float totalWidth = 0;

        for(int i = 0; i < text.length(); i++){
            totalWidth += 1.5F + Font.getCharacterWidth(String.valueOf(text.charAt(i)));
        }

        return totalWidth / 16F * fontSize;
    }

    public static char[] getFontIndex() {
        return fontIndex;
    }
}
