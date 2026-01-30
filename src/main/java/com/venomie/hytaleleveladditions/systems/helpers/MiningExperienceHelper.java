package com.venomie.hytaleleveladditions.systems.helpers;

import com.hypixel.hytale.server.core.Message;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.venomie.hytaleleveladditions.systems.constants.OreConstants;

public class MiningExperienceHelper {

    /**
     * Adds XP for a mined ore and notifies player if level increases.
     * Only applies for recognized ores.
     */
    public static void handleXpAndLevel(Player player, MiningLevelComponent mining, String blockId) {
        if (!OreConstants.isOre(blockId)) return;

        int oldLevel = mining.getLevel();

        // Add XP based on Ore type
        int xpToAdd = OreConstants.getXpForOre(blockId);
        mining.addXp(xpToAdd);

        int newLevel = mining.getLevel();
        if (newLevel > oldLevel) {
            player.sendMessage(Message.raw("Your Mining Level is now " + newLevel));
        }
    }
}
