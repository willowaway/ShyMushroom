package com.willowaway.shymushroom.systems;

import com.hypixel.hytale.builtin.mounts.NPCMountComponent;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.config.AttitudeGroup;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.systems.RoleBuilderSystem;
import com.willowaway.shymushroom.NibletPlugin;
import com.willowaway.shymushroom.component.PetComponent;
import com.willowaway.shymushroom.component.TameComponent;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class TameSystem extends HolderSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final ComponentType<EntityStore, TameComponent> tameComponentType = TameComponent.getComponentType();
    @Nonnull
    private final ComponentType<EntityStore, NPCEntity> npcEntityComponentType;
    @Nonnull
    private final Query<EntityStore> query;
    @Nonnull
    private final Set<Dependency<EntityStore>> dependencies;

    public TameSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcEntityComponentType) {
        this.npcEntityComponentType = npcEntityComponentType;
        this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, RoleBuilderSystem.class));
        this.query = Query.and(npcEntityComponentType, Query.not(NPCMountComponent.getComponentType()));
    }

    @Override
    public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
        NPCEntity npcEntity = holder.getComponent(this.npcEntityComponentType);
        if (npcEntity == null) {
            return;
        }

        Role role = npcEntity.getRole();
        if (role == null || !role.getRoleName().equals("Niblet")){
            return;
        }

        WorldSupport worldSupport = role.getWorldSupport();
        AttitudeGroup attitudeGroup = AttitudeGroup.getAssetMap().getAsset(worldSupport.getAttitudeGroup());
        if (attitudeGroup == null) {
            return;
        }

        TameComponent tameComponent = holder.ensureAndGetComponent(this.tameComponentType);
        if (tameComponent.getIsTame()) {
            try {
                NibletPlugin.getAttitudeField().set(worldSupport, Attitude.REVERED);
            } catch (IllegalAccessException e) {
                LOGGER.atSevere().log("Failed to set attitude for NPC to REVERED", e);
            }

            // Spawning no longer needs to be tracked for this entity
            boolean oldState = npcEntity.updateSpawnTrackingState(false);
            if (oldState) {
                LOGGER.atInfo().log("Stopped tracking entity spawning " + npcEntity.getRoleName(), Level.INFO);
            }
        }
    }

    @Override
    public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return this.query;
    }

    @Nonnull
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return this.dependencies;
    }
}
