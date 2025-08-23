package game.shared.world.creature;

import game.server.world.ServerPlayer;

import java.util.HashMap;

public class Creatures {
    public static HashMap<Class<? extends Creature>, String> creatureToId = new HashMap<>();
    public static HashMap<String, Class<? extends Creature>> idToCreature = new HashMap<>();

    public static void register(Class<? extends Creature> creature, String id) {
        creatureToId.put(creature, id);
        idToCreature.put(id, creature);
    }

    public static void init() {
        creatureToId.put(Player.class, "player");
        creatureToId.put(ServerPlayer.class, "player");
        idToCreature.put("player", OtherPlayer.class);
        register(ItemCreature.class, "item");
    }

    public static String getIdFor(Creature creature) {
        return creatureToId.get(creature.getClass());
    }

    public static Class<? extends Creature> getClassFor(String id) {
        return idToCreature.get(id);
    }
}
