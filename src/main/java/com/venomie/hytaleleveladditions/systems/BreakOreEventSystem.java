package com.venomie.hytaleleveladditions.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.venomie.hytaleleveladditions.ExamplePlugin;
import com.venomie.hytaleleveladditions.components.MiningLevelComponent;
import com.venomie.hytaleleveladditions.formulas.SkillFormula;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class BreakOreEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final ComponentType<EntityStore, MiningLevelComponent> miningComponent;

    // ==== CONFIGURATION CONSTANTS ====
    private static final int COAL_MIN_LEVEL = 5;
    private static final long XP_PER_ORE = 50;
    private static final String COAL_ITEM_ID = "Ingredient_Charcoal";
    private static final int COAL_ITEM_AMOUNT = 2;

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

        Vector3i blockPosInt = breakBlockEvent.getTargetBlock(); // Vector3i from the event
        Vector3f blockPosition = new Vector3f(blockPosInt.x, blockPosInt.y, blockPosInt.z);

        String blockId = breakBlockEvent.getBlockType().getId();

        if (!blockId.contains("Ore")) return;

        // Schedule ECS-safe mutation
        commandBuffer.run((s) -> {
            MiningLevelComponent mining = store.ensureAndGetComponent(ref, miningComponent);

            // 1️⃣ XP and Level handling
            handleXpAndLevel(player, mining);

            // 2️⃣ Drop handling
            handleDrop(player, mining, blockId, blockPosition, store, COAL_ITEM_AMOUNT);
        });
    }

    private void handleXpAndLevel(Player player, MiningLevelComponent mining) {
        mining.setXp(mining.getXp() + (int) XP_PER_ORE);

        int newLevel = SkillFormula.getLevelForXp(mining.getXp());
        if (newLevel > mining.getLevel()) {
            mining.setLevel(newLevel);
            player.sendMessage(Message.raw("🎉 Congratulations! Your Mining Level is now " + newLevel));
        }
    }

    /**
     * Handles item drops for a mined ore.
     *
     * @param player the player mining the ore
     * @param mining the player's mining component
     * @param blockId the block type ID
     * @param blockPosition block position (Vector3f)
     * @param store the ECS store
     * @param amount the quantity of the item to drop
     */
    private void handleDrop(Player player,
                            MiningLevelComponent mining,
                            String blockId,
                            Vector3f blockPosition,
                            Store<EntityStore> store,
                            int amount) {

        // Only allow coal drop if player has required level
        if (blockId.contains("Coal") && mining.getLevel() < COAL_MIN_LEVEL) {
            player.sendMessage(Message.raw("You need Mining level " + COAL_MIN_LEVEL + " to mine coal."));
            return;
        }

        // Generate item drop
        ItemStack dropItem = new ItemStack(COAL_ITEM_ID, amount);
        var itemEntityHolder = ItemComponent.generateItemDrop(
                store,
                dropItem,
                blockPosition.toVector3d().add(.5, .5, .5),
                Vector3f.ZERO,
                0, 0, 0
        );

        Objects.requireNonNull(player.getWorld()).execute(() -> {
            assert itemEntityHolder != null;
            player.getWorld().getEntityStore().getStore().addEntity(itemEntityHolder, AddReason.SPAWN);
        });

        player.sendMessage(Message.raw("You received " + dropItem.getItemId() + "!"));
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
