package com.venomie.hytaleleveladditions;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.venomie.hytaleleveladditions.commands.MiningCommand;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.venomie.hytaleleveladditions.systems.BreakOreEventSystem;
import com.venomie.hytaleleveladditions.systems.PlayerLevelMiningSystem;

public class ExamplePlugin extends JavaPlugin {
    private static ExamplePlugin instance;
    private ComponentType<EntityStore, MiningLevelComponent> miningComponent;


    public ExamplePlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static ExamplePlugin instance() {
        return instance;
    }

    public static ComponentType<EntityStore, MiningLevelComponent> getMiningComponentType() {
        return instance.miningComponent;
    }

    @Override
    protected void setup() {
        // Register component
        this.miningComponent = this.getEntityStoreRegistry().registerComponent(
                MiningLevelComponent.class,
                "MiningLevelComponent",
                MiningLevelComponent.CODEC
        );

        // Register systems safely
        this.getEntityStoreRegistry().registerSystem(new PlayerLevelMiningSystem(this.miningComponent));
        this.getEntityStoreRegistry().registerSystem(new BreakOreEventSystem());

        // Register commands
        this.getCommandRegistry().registerCommand(new MiningCommand());
    }
}


