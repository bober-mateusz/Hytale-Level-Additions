package com.venomie.hytaleleveladditions.systems.constants;

import java.util.*;

public final class OreConstants {
    private OreConstants() {}

    public static final String COPPER = "Ore_Copper_";
    public static final String IRON = "Ore_Iron_";
    public static final String THORIUM = "Ore_Thorium_";
    public static final String GOLD = "Ore_Gold_";
    public static final String COBALT = "Ore_Cobalt_";
    public static final String SILVER = "Ore_Silver_";
    public static final String ADAMANTITE = "Ore_Adamantite_";
    public static final String MITHRIL = "Ore_Mithril_";
    public static final String ONYXIUM = "Ore_Onyxium_";

    private static final Map<String, Integer> ORE_XP = new LinkedHashMap<>();
    private static final List<String> ORE_PREFIXES;

    static {
        // XP mapping
        ORE_XP.put(COPPER, 50);
        ORE_XP.put(IRON, 75);
        ORE_XP.put(THORIUM, 100);
        ORE_XP.put(GOLD, 125);
        ORE_XP.put(COBALT, 150);
        ORE_XP.put(SILVER, 175);
        ORE_XP.put(ADAMANTITE, 200);
        ORE_XP.put(MITHRIL, 250);
        ORE_XP.put(ONYXIUM, 300);

        ORE_PREFIXES = new ArrayList<>(ORE_XP.keySet());
    }

    /** Checks if a block ID starts with a known ore prefix */
    public static boolean isOre(String blockId) {
        for (String prefix : ORE_PREFIXES) {
            if (blockId.startsWith(prefix)) return true;
        }
        return false;
    }

    /** Gets XP for a given block ID, default 0 if not found */
    public static int getXpForOre(String blockId) {
        for (Map.Entry<String, Integer> entry : ORE_XP.entrySet()) {
            if (blockId.startsWith(entry.getKey())) return entry.getValue();
        }
        return 0;
    }
}

