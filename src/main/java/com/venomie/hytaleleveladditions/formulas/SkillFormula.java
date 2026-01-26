package com.venomie.hytaleleveladditions.formulas;

/**
 * Simple skill leveling formulas for XP ↔ Level conversions.
 * Can be extended to other skills like Mining, Sword, Magic, etc.
 */
public final class SkillFormula {

    private SkillFormula() {} // Utility class, no instantiation

    /**
     * Calculates the level for a given XP.
     * Ensures level starts at 1.
     *
     * @param xp total XP
     * @return level (>=1)
     */
    public static int getLevelForXp(long xp) {
        if (xp <= 0) return 1;

        int level = 1;
        long accumulatedXp = 0;

        // Simple incremental approach to find level from XP
        while (accumulatedXp <= xp) {
            long xpNeeded = xpForLevel(level);
            if (accumulatedXp + xpNeeded > xp) break;
            accumulatedXp += xpNeeded;
            level++;
        }

        return Math.max(level, 1);
    }

    /**
     * Calculates the total XP required to reach the given level from previous level.
     *
     * @param level target level (>=1)
     * @return XP needed to reach this level from previous level
     */
    public static long xpForLevel(int level) {
        if (level <= 1) return 0;
        return (long) Math.floor(100 * Math.pow(level - 1, 1.5));
    }

    /**
     * Calculates the XP required to reach the next level from current XP.
     *
     * @param currentXp current XP
     * @return XP needed for next level
     */
    public static long getXpToNextLevel(long currentXp) {
        int currentLevel = getLevelForXp(currentXp);
        long nextLevelXp = 0;
        for (int lvl = 1; lvl <= currentLevel; lvl++) {
            nextLevelXp += xpForLevel(lvl);
        }
        return nextLevelXp - currentXp + xpForLevel(currentLevel + 1);
    }
}
