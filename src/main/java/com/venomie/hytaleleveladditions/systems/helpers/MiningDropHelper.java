package com.venomie.hytaleleveladditions.systems.helpers;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;

import java.util.Objects;

/**
 * Helper for spawning extra drops from mined ores.
 * Only adds extra coal; vanilla drops remain untouched.
 */
public class MiningDropHelper {

    // ====== CONFIG ======
    private static final int COAL_MIN_LEVEL = 10;       // milestone minimum to drop coal
    private static final String COAL_ITEM_ID = "Ingredient_Charcoal";
    private static final int BASE_DROP_AMOUNT = 1;      // default quantity of extra coal
    private static final int DOUBLE_DROP_LEVEL = 30;    // level at which 2-coal chance starts
    private static final double DOUBLE_DROP_CHANCE = 0.5; // 50% chance for 2 coal
    private static final int MAX_DROP_AMOUNT = 2;

    /**
     * Adds extra coal drop for ore blocks based on milestones.
     * Does NOT override vanilla drops.
     */
    public static void handleExtraCoalDrop(MiningLevelComponent mining,
                                           String blockId,
                                           Vector3f blockPos,
                                           EntityStore store,
                                           World world) {

        int level = mining.getLevel();

        // Only ores can drop extra coal
        if (!blockId.contains("Ore")) return;
        if (level < COAL_MIN_LEVEL) return;

        // Milestone-based chance to drop coal
        double chance = getCoalDropChance(level);
        if (Math.random() > chance) return;

        // Determine amount: default 1, after DOUBLE_DROP_LEVEL maybe 2
        int amount = BASE_DROP_AMOUNT;
        if (level >= DOUBLE_DROP_LEVEL && Math.random() < DOUBLE_DROP_CHANCE) {
            amount = MAX_DROP_AMOUNT;
        }

        // Spawn extra coal in ECS-safe way
        ItemStack dropItem = new ItemStack(COAL_ITEM_ID, amount);
        var entity = ItemComponent.generateItemDrop(store.getStore(),
                dropItem,
                blockPos.toVector3d().add(.5, .5, .5),
                Vector3f.ZERO,
                0, 0, 0);

        if (entity != null) {
            world.execute(() -> world.getEntityStore().getStore().addEntity(entity, AddReason.SPAWN));
        }
    }

    /**
     * Milestone-based chance for extra coal.
     * Level 10 → 0% → 10% → 30% → 50% … Level 50 → 100%.
     */
    private static double getCoalDropChance(int level) {
        if (level < 10) return 0.0;
        else if (level < 20) return 0.3;
        else if (level < 30) return 0.5;
        else if (level < 40) return 0.7;
        else if (level < 50) return 0.9;
        else return 1.0;
    }
}
