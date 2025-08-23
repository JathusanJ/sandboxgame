package game.shared.world.items;

public class PickaxeItem extends Item {
    public PickaxeTier tier;

    public PickaxeItem(PickaxeTier tier) {
        this.tier = tier;
    }

    public enum PickaxeTier {
        WOODEN(1F),
        STONE(1.5F),
        IRON(2F),
        DIAMOND(2.5F);

        float strength;

        PickaxeTier(float strength) {
            this.strength = strength;
        }

        public float getStrength() {
            return this.strength;
        }
    }
}
