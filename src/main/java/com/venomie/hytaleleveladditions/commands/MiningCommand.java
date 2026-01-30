package com.venomie.hytaleleveladditions.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.venomie.hytaleleveladditions.ExamplePlugin;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.venomie.hytaleleveladditions.formulas.SkillFormula;
import org.jetbrains.annotations.NotNull;

/**
 * Admin/player command for inspecting and modifying Mining levels.
 *
 * Usage:
 * /Mining                          → View current Mining level and XP
 * /Mining --Operation=AddLevel --Levels=5 → Add 5 Mining levels
 * /Mining --Operation=Reset         → Reset Mining level to 1 (XP = 0)
 *
 * Notes:
 * - Levels are computed dynamically from total XP using SkillFormula.
 * - Only integer XP values are supported; fractional XP is not used.
 * - Operation and Levels are named optional arguments.
 */
public class MiningCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    /** Optional operation argument: "AddLevel" or "Reset" */
    private final OptionalArg<String> operationArg;

    /** Optional levels argument for AddLevel operation */
    private final OptionalArg<Integer> levelsToAddArg;

    public MiningCommand() {
        super("Mining", "Get your Mining Level Details or modify it", false);

        this.operationArg = withOptionalArg(
                "Operation",
                "AddLevel or Reset",
                ArgTypes.STRING
        );

        this.levelsToAddArg = withOptionalArg(
                "Levels",
                "Amount of levels to add (required for AddLevel)",
                ArgTypes.INTEGER
        );
    }

    @Override
    protected void execute(
            @NotNull CommandContext ctx,
            @NotNull Store<EntityStore> store,
            @NotNull Ref<EntityStore> ref,
            @NotNull PlayerRef playerRef,
            @NotNull World world
    ) {
        // ECS-safe execution
        world.execute(() -> {
            MiningLevelComponent mining = store.getComponent(
                    ref,
                    ExamplePlugin.getMiningComponentType()
            );

            if (mining == null) {
                ctx.sendMessage(Message.raw("Error: Player has no Mining component!"));
                return;
            }

            // Named optional arguments
            String operation = operationArg.get(ctx); // e.g., AddLevel or Reset
            Integer levelsToAdd = levelsToAddArg.get(ctx); // only relevant for AddLevel

            // === VIEW MODE ===
            if (operation == null) {
                sendMiningUi(ctx, mining);
                return;
            }

            // === OPERATION DISPATCH ===
            switch (operation.toLowerCase()) {
                case "addlevel" -> {
                    if (levelsToAdd == null) {
                        ctx.sendMessage(Message.raw("You must specify the number of levels to add using --Levels=<amount>."));
                        return;
                    }
                    addLevel(ctx, mining, levelsToAdd);
                }
                case "reset" -> resetLevel(ctx, mining);
                default -> ctx.sendMessage(Message.raw("Unknown operation: " + operation));
            }
        });
    }

    /* =======================
       UI / Display
    ======================= */
    private void sendMiningUi(CommandContext ctx, MiningLevelComponent mining) {
        int totalXp = mining.getXp();
        int level = mining.getLevel();

        int levelStartXp = SkillFormula.totalXpForLevel(level);
        int xpForThisLevel = SkillFormula.xpForLevel(level);
        int xpIntoLevel = Math.max(0, totalXp - levelStartXp);

        ctx.sendMessage(Message.raw("=== Mining UI ==="));
        ctx.sendMessage(Message.raw("Level: " + level));
        ctx.sendMessage(Message.raw("XP: " + xpIntoLevel + " / " + xpForThisLevel));
        ctx.sendMessage(Message.raw("Total XP: " + totalXp));
    }



    /* =======================
       MUTATION / Modifying Levels
    ======================= */
    private void addLevel(CommandContext ctx, MiningLevelComponent mining, int levelsToAdd) {
        int currentLevel = mining.getLevel();
        int targetLevel = currentLevel + levelsToAdd;

        int xpToAdd = SkillFormula.totalXpForLevel(targetLevel)
                - SkillFormula.totalXpForLevel(currentLevel);

        mining.addXp(xpToAdd);

        ctx.sendMessage(Message.raw("Mining level increased to " + targetLevel));
    }

    private void resetLevel(CommandContext ctx, MiningLevelComponent mining) {
        mining.setXp(0);
        ctx.sendMessage(Message.raw("Mining level has been reset to 1."));
    }
}
