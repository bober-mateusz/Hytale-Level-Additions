package com.venomie.hytaleleveladditions.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;

/**
 * PlayerLevelMiningSystem is responsible for initializing and managing
 * a player's MiningLevelComponent when a player enters the world.
 * <p>
 * This system is a RefSystem, meaning it operates on individual entity references
 * instead of processing large archetype chunks.
 */
public class PlayerLevelMiningSystem extends RefSystem<EntityStore> {

    /** Reference to the MiningLevelComponent type for ECS access */
    private final ComponentType<EntityStore, MiningLevelComponent> miningComponent;

    /**
     * Constructs a new PlayerLevelMiningSystem.
     *
     * @param miningComponent the ECS ComponentType representing MiningLevelComponent
     */
    public PlayerLevelMiningSystem(ComponentType<EntityStore, MiningLevelComponent> miningComponent) {
        this.miningComponent = miningComponent;
    }

    /**
     * Defines the query for this system.
     * Only entities with a PlayerRef component will be processed.
     *
     * @return the query matching player entities
     */
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    /**
     * Called when a player entity is added to the world.
     * Ensures the player has a MiningLevelComponent and sends a welcome message
     * with the current mining level.
     *
     * @param ref reference to the entity
     * @param reason reason for addition
     * @param store ECS store containing entity components
     * @param commandBuffer ECS command buffer for safe mutations
     */
    @Override
    public void onEntityAdded(
            @NotNull Ref<EntityStore> ref,
            @NotNull AddReason reason,
            @NotNull Store<EntityStore> store,
            @NotNull CommandBuffer<EntityStore> commandBuffer
    ) {
        // Get the PlayerRef component for sending messages
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        assert playerRef != null;

        // Schedule ECS-safe mutation to initialize MiningLevelComponent
        commandBuffer.run((s) -> {
            // Ensure the entity has a MiningLevelComponent; create if missing
            MiningLevelComponent mining = store.ensureAndGetComponent(ref, miningComponent);

            // Notify player of their current mining level
            playerRef.sendMessage(
                    Message.raw("Mining Level loaded! Level: " + mining.getLevel())
            );
        });
    }

    /**
     * Called when a player entity is removed from the world.
     * Currently empty but reserved for cleanup logic if needed in the future.
     *
     * @param ref reference to the entity
     * @param removeReason reason for removal
     * @param store ECS store containing entity components
     * @param commandBuffer ECS command buffer for safe mutations
     */
    @Override
    public void onEntityRemove(
            @NotNull Ref<EntityStore> ref,
            @NotNull RemoveReason removeReason,
            @NotNull Store<EntityStore> store,
            @NotNull CommandBuffer<EntityStore> commandBuffer
    ) {
        // Placeholder for cleanup logic if we want to save or reset mining state
    }
}