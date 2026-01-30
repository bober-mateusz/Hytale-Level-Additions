package com.venomie.hytaleleveladditions.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.venomie.hytaleleveladditions.ExamplePlugin;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.venomie.hytaleleveladditions.systems.helpers.MiningExperienceHelper;
import com.venomie.hytaleleveladditions.systems.helpers.MiningDropHelper;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * Event system handling when a player breaks an ore block.
 * - Grants Mining XP
 * - Adds milestone-based extra coal drops
 * - Does not replace vanilla drops
 */
public class BreakOreEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final ComponentType<EntityStore, MiningLevelComponent> miningComponent;

    public BreakOreEventSystem() {
        super(BreakBlockEvent.class);
        this.miningComponent = ExamplePlugin.getMiningComponentType();
    }

    @Override
    public void handle(int index,
                       @NotNull ArchetypeChunk<EntityStore> archetypeChunk,
                       @NotNull Store<EntityStore> store,
                       @NotNull CommandBuffer<EntityStore> commandBuffer,
                       @NotNull BreakBlockEvent breakBlockEvent) {

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        // Convert block position to Vector3f
        Vector3i blockPosInt = breakBlockEvent.getTargetBlock();
        Vector3f blockPosition = new Vector3f(blockPosInt.x, blockPosInt.y, blockPosInt.z);

        String blockId = breakBlockEvent.getBlockType().getId();

        // Only ores should trigger XP and extra coal
        if (!blockId.contains("Ore")) return;

        // ECS-safe mutation
        commandBuffer.run((s) -> {
            MiningLevelComponent mining = store.getComponent(ref, miningComponent);

            // 1️⃣ Add Mining XP and handle level-ups
            assert mining != null;
            MiningExperienceHelper.handleXpAndLevel(player, mining, blockId);

            // 2️⃣ Handle milestone-based extra coal drop (does not replace vanilla drops)
            MiningDropHelper.handleExtraCoalDrop(mining, blockId, blockPosition, player.getWorld().getEntityStore(), player.getWorld());
        });
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
