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
import org.jetbrains.annotations.NotNull;


public class MiningCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final OptionalArg<String> operationArg;

    private final OptionalArg<Integer> levelsToAddArg;

    public MiningCommand() {
        super("Mining", "Get your Mining Level Details", false);

        this.operationArg = withOptionalArg(
                "Operation",
                "AddLevel",
                ArgTypes.STRING
        );

        this.levelsToAddArg = withOptionalArg(
                "Levels",
                "Amount of levels to add",
                ArgTypes.INTEGER
        );
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        world.execute(() -> {
            // This runs safely on the world's thread, allowing ECS component access
            // Send debug messages to the player
            MiningLevelComponent miningComponent = store.getComponent(ref, ExamplePlugin.getMiningComponentType());
            if(miningComponent == null){
                ctx.sendMessage(Message.raw("Error: Player has no Mining component!"));
                return;
            }
            String operation = operationArg.get(ctx);
            Integer levelsToAdd = levelsToAddArg.get(ctx);

            if (operation == null || levelsToAdd == null) {
                ctx.sendMessage(Message.raw("=== Mining Debug ==="));
                ctx.sendMessage(Message.raw("Mining component: PRESENT"));
                ctx.sendMessage(Message.raw("Player mining level is : " + miningComponent.getLevel()));
                return;
            }

            int currentLevel = miningComponent.getLevel();
            int targetLevel = currentLevel + levelsToAdd;

            miningComponent.setLevel(targetLevel);
        });
    }
}
