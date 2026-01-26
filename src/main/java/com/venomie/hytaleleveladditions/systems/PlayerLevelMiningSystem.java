package com.venomie.hytaleleveladditions.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import com.venomie.hytaleleveladditions.ExamplePlugin;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;

public class PlayerLevelMiningSystem extends RefSystem<EntityStore> {

    private final ComponentType<EntityStore, MiningLevelComponent> miningComponent;

    public PlayerLevelMiningSystem(
            ComponentType<EntityStore, MiningLevelComponent> miningComponent
    ) {
        this.miningComponent = miningComponent;
    }

    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @Override
    public void onEntityAdded(
            @NotNull Ref<EntityStore> ref,
            @NotNull AddReason reason,
            @NotNull Store<EntityStore> store,
            @NotNull CommandBuffer<EntityStore> commandBuffer
    ) {
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        assert playerRef != null;

        // Schedule the ECS mutation safely
        commandBuffer.run((s) -> {
            MiningLevelComponent mining = store.ensureAndGetComponent(ref, miningComponent);
            playerRef.sendMessage(
                    Message.raw("Mining Level loaded! Level: " + mining.getLevel())
            );
        });
    }


    @Override
    public void onEntityRemove(@NotNull Ref<EntityStore> ref, @NotNull RemoveReason removeReason, @NotNull Store<EntityStore> store, @NotNull CommandBuffer<EntityStore> commandBuffer) {

    }
}


