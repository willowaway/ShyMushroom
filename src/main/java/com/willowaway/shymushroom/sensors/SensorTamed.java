package com.willowaway.shymushroom.sensors;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.SensorBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.willowaway.shymushroom.builders.BuilderSensorTamed;
import com.willowaway.shymushroom.component.TameComponent;

import javax.annotation.Nonnull;

public class SensorTamed extends SensorBase {
    protected final boolean isTamed;

    public SensorTamed(@Nonnull BuilderSensorTamed builderSensorTamed, @Nonnull BuilderSupport builderSupport) {
        super(builderSensorTamed);
        this.isTamed = builderSensorTamed.getIsTamed(builderSupport);
    }

    public boolean matches(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, double dt, @Nonnull Store<EntityStore> store) {
        TameComponent tameComponent = store.getComponent(ref, TameComponent.getComponentType());
        if (tameComponent == null) {
            return false;
        } else {
            return super.matches(ref, role, dt, store) && tameComponent.getIsTame() == this.isTamed;
        }
    }

    @Override
    public InfoProvider getSensorInfo() {
        return null;
    }
}
