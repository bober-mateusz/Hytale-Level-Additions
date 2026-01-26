package com.venomie.hytaleleveladditions.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;

public class MiningLevelComponent implements Component<EntityStore> {

    private int level;
    private int xp;

    // ✅ Hytale style Codec
    public static final BuilderCodec<MiningLevelComponent> CODEC =
            BuilderCodec.builder(MiningLevelComponent.class, MiningLevelComponent::new)
                    .addField(new KeyedCodec<>("Level", Codec.INTEGER),
                            (data, value) -> data.level = value, // setter
                            data -> data.level)                 // getter
                    .addField(new KeyedCodec<>("Xp", Codec.INTEGER),
                            (data, value) -> data.xp = value,   // setter
                            data -> data.xp)                    // getter
                    .build();

    // constructor
    public MiningLevelComponent() {
        this.level = 1;
        this.xp = 0;
    }

    // copy constructor for cloning
    public MiningLevelComponent(MiningLevelComponent clone) {
        this.level = clone.level;
        this.xp = clone.xp;
    }

    @Nullable
    @Override
    public Component<EntityStore> clone() {
        return new MiningLevelComponent(this);
    }


    // getters/setters
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getXp() { return xp; }
    public void setXp(int xp) { this.xp = xp; }
}
