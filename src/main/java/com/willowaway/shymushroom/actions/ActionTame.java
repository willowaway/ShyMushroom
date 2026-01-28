package com.willowaway.shymushroom.actions;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.willowaway.shymushroom.NibletPlugin;
import com.willowaway.shymushroom.builders.BuilderActionTame;
import com.willowaway.shymushroom.component.PetComponent;
import com.willowaway.shymushroom.component.TameComponent;
import it.unimi.dsi.fastutil.objects.ObjectList;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;

public class ActionTame extends ActionBase {

    protected final Set<String> tameItems;
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public ActionTame(@Nonnull BuilderActionTame builderActionTame, @Nonnull BuilderSupport builderSupport) {
        super(builderActionTame);
        this.tameItems = new HashSet<>(Arrays.asList(builderActionTame.getTameItems(builderSupport)));
        builderSupport.requireAttitudeOverrideMemory();
    }

    @Override
    public boolean canExecute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        return super.canExecute(ref, role, sensorInfo, dt, store) && role.getStateSupport().getInteractionIterationTarget() != null;
    }

    @Override
    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> playerRefEntityStore = role.getStateSupport().getInteractionIterationTarget();
        if (playerRefEntityStore == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get NPC Target's Ref Entity Store");
            return false;
        }

        PlayerRef playerRef = store.getComponent(playerRefEntityStore, PlayerRef.getComponentType());
        if (playerRef == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get Player Ref");
            return false;
        }

        Player player = store.getComponent(playerRefEntityStore, Player.getComponentType());
        if (player == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get Player");
            return false;
        }

        ComponentType<EntityStore, NPCEntity> npcEntityComponentType = NPCEntity.getComponentType();
        if (npcEntityComponentType == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get NPC Entity Component Type of NPC");
            return false;
        }

        NPCEntity npcEntity = store.getComponent(ref, npcEntityComponentType);
        if (npcEntity == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get NPC Entity of NPC");
            return false;
        }

        TameComponent tameComponent = store.getComponent(ref, TameComponent.getComponentType());
        if (tameComponent == null){
            LOGGER.atSevere().log("Action Tame: Failed to get Tame Component of NPC");
            return false;
        }

        UUIDComponent petId = store.getComponent(ref, UUIDComponent.getComponentType());
        if (petId == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get UUID of NPC");
            return false;
        }

        World world = player.getWorld();
        if (world == null) {
            return false;
        }

        TransformComponent entityTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (entityTransformComponent == null) {
            LOGGER.atSevere().log("Action Tame: Failed to get position of NPC");
            return false;
        }
        Vector3d petPos = entityTransformComponent.getPosition();
        Vector3d particlePos = new Vector3d(petPos.x, petPos.y + 0.5, petPos.z);

        boolean isTamed = tameComponent.attemptToTame();
        if (!isTamed) {
            ParticleUtil.spawnParticleEffect("Question", particlePos, Collections.singletonList(playerRefEntityStore), store);
            SoundUtil.playSoundEvent2d(SoundEvent.getAssetMap().getIndex("SFX_Meerkat_Alerted"), SoundCategory.SFX, store);
            playerRef.sendMessage(Message.raw("You have failed to tame a wild %s".formatted(npcEntity.getRoleName())));
            return false;
        }

        ParticleUtil.spawnParticleEffect("Hearts", particlePos, Collections.singletonList(playerRefEntityStore), store);
        PetComponent petComponent =  store.ensureAndGetComponent(playerRefEntityStore, PetComponent.getComponentType());
        petComponent.set(petId.getUuid(), world.getName(), "Nib");

        WorldSupport worldSupport = role.getWorldSupport();
        try {
            NibletPlugin.getAttitudeField().set(worldSupport, Attitude.REVERED);
        } catch (IllegalAccessException e) {
            LOGGER.atSevere().log("Failed to set attitude of NPC to REVERED", e);
            return false;
        }

        boolean oldState = npcEntity.updateSpawnTrackingState(false);
        if (oldState) {
            LOGGER.atInfo().log("Stopped tracking entity state for entity: %s".formatted(npcEntity.getRoleName()));
        }

        playerRef.sendMessage(Message.raw("You have tamed a wild %s".formatted(npcEntity.getRoleName())));
        return true;
    }
}
