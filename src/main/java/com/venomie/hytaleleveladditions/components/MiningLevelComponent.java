package com.venomie.hytaleleveladditions.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;
import com.venomie.hytaleleveladditions.formulas.SkillFormula;

/**
 * Component representing a player's Mining skill.
 * Stores total XP and computes level dynamically via SkillFormula.
 *
 * XP is stored as an integer. Level is computed from XP using
 * SkillFormula.getLevelForXp(xp).
 */
public class MiningLevelComponent implements Component<EntityStore> {

    /** Total XP earned by the player in Mining */
    private int xp;

    // ===============================
    // Hytale Codec for ECS serialization
    // ===============================
    public static final BuilderCodec<MiningLevelComponent> CODEC =
            BuilderCodec.builder(MiningLevelComponent.class, MiningLevelComponent::new)
                    .addField(new KeyedCodec<>("Xp", Codec.INTEGER),
                            (data, value) -> data.xp = value,   // setter
                            data -> data.xp)                    // getter
                    .build();

    // ===============================
    // Constructors
    // ===============================

    /** Default constructor, starts at 0 XP (level 1) */
    public MiningLevelComponent() {
        this.xp = 0;
    }

    /** Copy constructor for cloning ECS components */
    public MiningLevelComponent(MiningLevelComponent clone) {
        this.xp = clone.xp;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new MiningLevelComponent(this);
    }

    // ===============================
    // Public API
    // ===============================

    /** Returns the player's current Mining level (computed from XP) */
    public int getLevel() {
        return SkillFormula.getLevelForXp(xp);
    }

    /** Returns the total XP in Mining */
    public int getXp() {
        return xp;
    }

    /**
     * Adds XP to the player.
     * XP cannot go below 0.
     *
     * @param amount XP to add (can be negative)
     */
    public void addXp(int amount) {
        this.xp = Math.max(0, this.xp + amount);
    }

    /**
     * Sets the player's total XP.
     * XP cannot go below 0.
     *
     * @param xp total XP to set
     */
    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }

    // ===============================
    // Internal utility
    // ===============================

    /**
     * Sets the component's XP to match a specific level.
     * This is used internally for admin commands (e.g., "set level").
     *
     * @param mining the component to modify
     * @param targetLevel the level to set
     */
    private void setMiningLevel(MiningLevelComponent mining, int targetLevel) {
        long xpForLevel = SkillFormula.totalXpForLevel(targetLevel);
        mining.setXp((int) xpForLevel);
    }
}
