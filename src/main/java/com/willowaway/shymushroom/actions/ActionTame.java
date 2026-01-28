package com.willowaway.shymushroom.actions;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        Ref<EntityStore> refEntityStore = role.getStateSupport().getInteractionIterationTarget();
        if (refEntityStore == null) {
            return false;
        }

        PlayerRef playerRef = store.getComponent(refEntityStore, PlayerRef.getComponentType());
        if (playerRef == null) {
            return false;
        }

        Player player = store.getComponent(refEntityStore, Player.getComponentType());
        if (player == null) {
            return false;
        }

        ComponentType<EntityStore, NPCEntity> npcEntityComponentType = NPCEntity.getComponentType();
        if (npcEntityComponentType == null) {
            return false;
        }

        NPCEntity npcEntity = store.getComponent(ref, npcEntityComponentType);
        if (npcEntity == null) {
            return false;
        }

        WorldSupport worldSupport = role.getWorldSupport();
        TameComponent tameComponent = store.getComponent(ref, TameComponent.getComponentType());
        if (tameComponent == null){
            return false;
        }

        UUIDComponent petId = store.getComponent(ref, UUIDComponent.getComponentType());
        if (petId == null) {
            return false;
        }

        World world = player.getWorld();
        if (world == null) {
            return false;
        }

        tameComponent.setIsTame(true);
        PetComponent petComponent =  store.ensureAndGetComponent(refEntityStore, PetComponent.getComponentType());
        petComponent.set(petId.getUuid(), world.getName(), "Nib");

        try {
            NibletPlugin.getAttitudeField().set(worldSupport, Attitude.REVERED);
        } catch (IllegalAccessException e) {
            LOGGER.atSevere().log("Failed to set attitude of NPC to REVERED", e);
            return false;
        }
//
//        boolean oldState = npcEntity.updateSpawnTrackingState(false);
//        if (oldState) {
//            LOGGER.atInfo().log("Stopped tracking entity state for entity: %s".formatted(npcEntity.getRoleName()));
//        }

        playerRef.sendMessage(Message.raw("You have tamed a wild %s".formatted(npcEntity.getRoleName())));
        return true;
    }
}
