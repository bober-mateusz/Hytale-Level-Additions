package com.venomie.hytaleleveladditions.formulas;

/**
 * SkillFormula provides utility methods for converting between XP and Levels
 * for various skills (e.g., Mining, Sword, Magic).
 *
 * Levels are computed incrementally based on total XP. XP is integer-based,
 * and formulas allow adjustment per skill type. Level calculations start at 1.
 * Max level is not enforced by this class (but you may implement a cap elsewhere, e.g., 100).
 */
public final class SkillFormula {

    private SkillFormula() {
        // Utility class; prevent instantiation
    }

    // ===============================
    // LEVEL CALCULATIONS
    // ===============================

    /**
     * Calculates the level corresponding to a given total XP.
     * Level starts at 1 and increments until the XP required for the next level
     * exceeds the given total XP.
     *
     * @param xp total XP (integer)
     * @return level >= 1
     */
    public static int getLevelForXp(long xp) {
        if (xp <= 0) return 1;

        int level = 1;
        long accumulatedXp = 0;

        while (true) {
            long xpNeeded = xpForLevel(level + 1);
            if (accumulatedXp + xpNeeded > xp) break;
            accumulatedXp += xpNeeded;
            level++;
        }

        return level;
    }

    /**
     * Returns the XP required to go from (level - 1) → level.
     * Level 1 requires 0 XP.
     *
     * @param level target level (integer)
     * @return XP required to reach this level
     */
    public static int xpForLevel(int level) {
        if (level <= 1) return 0;
        // Exponential growth formula: rounded for integer XP
        return (int) Math.round(100 * Math.pow(level - 1, 1.5));
    }

    /**
     * Returns the cumulative XP required to reach a given level from level 1.
     *
     * @param level target level (integer)
     * @return total XP needed to reach this level
     */
    public static int totalXpForLevel(int level) {
        int total = 0;
        for (int lvl = 2; lvl <= level; lvl++) {
            total += xpForLevel(lvl);
        }
        return total;
    }

    /**
     * Returns the XP the player has accumulated **within their current level**.
     *
     * @param totalXp player's total XP
     * @return XP progress into the current level
     */
    public static int xpIntoLevel(int totalXp) {
        int level = getLevelForXp(totalXp);
        return totalXp - totalXpForLevel(level - 1);
    }

    /**
     * Returns the remaining XP required to reach the next level.
     *
     * @param totalXp player's total XP
     * @return XP needed to reach next level
     */
    public static int getXpToNextLevel(int totalXp) {
        int level = getLevelForXp(totalXp);
        int xpIntoLevel = xpIntoLevel(totalXp);
        return xpForLevel(level + 1) - xpIntoLevel;
    }
}
