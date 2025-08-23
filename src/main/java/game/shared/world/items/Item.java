package game.shared.world.items;

import game.client.ui.text.Language;
import game.shared.world.World;
import game.shared.world.creature.Player;
import org.joml.Vector3i;

public class Item {
    public String id;

    public void onUse(ItemUsageContext context) {

    }

    public void setItemId(String id) {
        this.id = id;
    }

    public record ItemUsageContext(World world, Player player, ItemStack itemStack, Vector3i blockPosition, Vector3i normal) {}

    public String getName() {
        return Language.translate("item." + this.id);
    }
}
