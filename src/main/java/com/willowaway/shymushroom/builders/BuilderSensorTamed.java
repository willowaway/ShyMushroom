package com.willowaway.shymushroom.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.BooleanHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderSensorBase;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.willowaway.shymushroom.sensors.SensorTamed;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class BuilderSensorTamed extends BuilderSensorBase {
    protected final BooleanHolder isTamed = new BooleanHolder();

    public boolean getIsTamed(@Nonnull BuilderSupport builderSupport) {
        return this.isTamed.get(builderSupport.getExecutionContext());
    }

    @Override
    public @Nullable String getShortDescription() {
        return "Sensor to check if entity is tamed.";
    }

    @Override
    public @Nullable String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    public @Nullable Sensor build(BuilderSupport builderSupport) {
        return new SensorTamed(this, builderSupport);
    }

    @Override
    public @Nullable BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Nonnull
    public Builder<Sensor> readConfig(@Nonnull JsonElement data) {
        this.getBoolean(
                data,
                "IsTamed",
                this.isTamed,
                true,
                BuilderDescriptorState.Stable,
                "Is entity tamed or not",
                null
        );
        return this;
    }
}
