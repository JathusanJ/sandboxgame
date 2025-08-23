package game.shared.world.items;

import game.shared.util.json.WrappedJsonObject;
import game.shared.world.creature.Player;

public class ItemStack {
    public Item item;
    public int amount = 1;

    public ItemStack(Item item) {
        this.item = item;
    }

    public void decreaseByUnlessInCreative(int amount, Player player) {
        if(player.gamemode != Player.Gamemode.CREATIVE) {
            this.decreaseBy(amount);
        }
    }

    public int decreaseBy(int amount) {
        this.amount = this.amount - amount;
        if(this.amount < 1) {
            int remainder = Math.abs(this.amount);
            this.amount = 0;
            this.item = Items.AIR;
            return remainder;
        }

        return 0;
    }

    public int increaseBy(int amount) {
        this.amount = this.amount + amount;
        if(this.amount > 64) {
            int remainder = this.amount - 64;
            this.amount = 64;
            return remainder;
        }

        return 0;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void saveAsJson(WrappedJsonObject json) {
        json.put("id", this.getItem().id);
        json.put("amount", this.amount);
    }

    public static ItemStack readFromJson(WrappedJsonObject json) {
        Item item = Items.idToItem.get(json.getString("id"));
        if(item == null) return null;

        ItemStack stack = new ItemStack(item);
        stack.setAmount(json.getInt("amount"));

        return stack;
    }

    public record ItemStackJsonFormat(String item, int amount) {}
}
