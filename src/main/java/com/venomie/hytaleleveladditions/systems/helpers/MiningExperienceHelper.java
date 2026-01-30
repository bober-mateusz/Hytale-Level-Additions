package com.venomie.hytaleleveladditions.systems.helpers;

import com.hypixel.hytale.server.core.Message;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;

public class MiningExperienceHelper {

    private static final int XP_PER_ORE = 50;

    /**
     * Adds XP for a mined ore and notifies player if level increases.
     */
    public static void handleXpAndLevel(Player player, MiningLevelComponent mining, String blockId) {
        int oldLevel = mining.getLevel();

        // Add XP (you could vary XP per block later)
        mining.addXp(XP_PER_ORE);

        int newLevel = mining.getLevel();
        if (newLevel > oldLevel) {
            player.sendMessage(Message.raw("Your Mining Level is now " + newLevel));
        }
    }
}
