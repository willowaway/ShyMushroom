package com.willowaway.shymushroom;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.WorldSupport;
import com.willowaway.shymushroom.builders.BuilderActionConsumeHeldItem;
import com.willowaway.shymushroom.builders.BuilderActionTame;
import com.willowaway.shymushroom.builders.BuilderSensorTamed;
import com.willowaway.shymushroom.component.TameComponent;
import com.willowaway.shymushroom.config.PetConfig;
import com.willowaway.shymushroom.model.Pet;
import com.willowaway.shymushroom.systems.TameSystem;
import com.willowaway.shymushroom.util.PetHelper;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.UUID;

public class NibletPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public static ComponentType<EntityStore, TameComponent> tameComponent;
    private static final Field ATTITUDE_FIELD;
    public final Config<PetConfig> config;
    @Getter
    private static NibletPlugin instance;

    public static Field getAttitudeField() {
        return ATTITUDE_FIELD;
    }

    public NibletPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Hello from %s version %s", this.getName(), this.getManifest().getVersion().toString());
        this.config = this.withConfig("PetConfig", PetConfig.CODEC);
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Registering Tame Component");
        tameComponent = this.getEntityStoreRegistry().registerComponent(TameComponent.class, "Tame", TameComponent.CODEC);
        LOGGER.atInfo().log("Registering on Player Ready Event");
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
        LOGGER.atInfo().log("Registering on Player Disconnect Event");
        this.getEventRegistry().register(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
    }

    @Override
    protected void start(){
        this.config.save();
        PetConfig config = this.config.get();
        PetHelper.setup(config);

        LOGGER.atInfo().log("Registering Niblet Tame System");
        ComponentType<EntityStore, NPCEntity> npcComponentType = NPCEntity.getComponentType();
        if (npcComponentType == null) {
            LOGGER.atSevere().log("Failed to Register Niblet Tame System. NPCEntity ComponentType was null");
            return;
        }
        this.getEntityStoreRegistry().registerSystem(new TameSystem(npcComponentType));

        NPCPlugin.get().registerCoreComponentType("Tame", BuilderActionTame::new);
        NPCPlugin.get().registerCoreComponentType("Tamed", BuilderSensorTamed::new);
        NPCPlugin.get().registerCoreComponentType("ConsumeHeldItem", BuilderActionConsumeHeldItem::new);
    }

    private void onPlayerReady(@Nonnull PlayerReadyEvent event) {
        Player player = event.getPlayer();

        World world = player.getWorld();
        if (world == null) {
            LOGGER.atSevere().log("Failed to spawn pet: world was null");
            return;
        }

        Ref<EntityStore> playerRef = event.getPlayerRef();
        if (!playerRef.isValid()) {
            LOGGER.atSevere().log("Failed to spawn pet: playerRef was NOT valid");
            return;
        }

        UUID playerId = player.getUuid();
        if (playerId == null) {
            LOGGER.atSevere().log("Failed to spawn pet: playerId was null");
            return;
        }
        LOGGER.atInfo().log("Player added to world: " + world.getName() + ". Player: " + playerId + ", spawning Niblet as pet");

        Ref<EntityStore> playerEntityRef = player.getReference();
        if (playerEntityRef == null) {
            LOGGER.atSevere().log("Failed to spawn pet: playerEntityRef was null");
            return;
        }

        if (!playerEntityRef.isValid()) {
            LOGGER.atSevere().log("Failed to spawn pet: playerEntityRef is NOT valid.");
            return;
        }

        Store<EntityStore> store = playerEntityRef.getStore();
        TransformComponent playerTransform = store.getComponent(playerEntityRef, TransformComponent.getComponentType());
        if (playerTransform == null) {
            LOGGER.atSevere().log("Failed to spawn pet: playerTransform cannot be null");
            return;
        }

        Vector3d spawnPos = playerTransform.getPosition().add(new Vector3d(1, 0, 1));
        Vector3f spawnRotation = new Vector3f();

        world.execute(() -> {
            Store<EntityStore> worldStore = world.getEntityStore().getStore();
            NPCPlugin npcPlugin = NPCPlugin.get();
            if (npcPlugin == null) {
                LOGGER.atSevere().log("Failed to spawn pet: npcPlugin cannot be null");
                return;
            }

            int roleIndex = npcPlugin.getIndex("Niblet");
            if (roleIndex < 0) {
                LOGGER.atSevere().log("Failed to spawn pet: roleIndex not found for Niblet");
                return;
            }

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Niblet");
            if (modelAsset == null) {
                LOGGER.atSevere().log("Failed to spawn pet: Model Asset for 'Niblet' not found");
                return;
            }

            Model model = Model.createScaledModel(modelAsset, 1.0f);
            Pair<Ref<EntityStore>, NPCEntity> spawnResult = npcPlugin.spawnEntity(worldStore, roleIndex, spawnPos, spawnRotation, model, null, null);

            if (spawnResult == null || spawnResult.first() == null || !spawnResult.first().isValid()) {
                LOGGER.atSevere().log("Failed to spawn pet: npcPlugin.spawnEntity returned null");
                return;
            } else {
                LOGGER.atInfo().log("Successfully spawned pet Niblet");
            }

            Ref<EntityStore> entityRef = spawnResult.first();
            UUIDComponent uuidComponent = worldStore.getComponent(entityRef, UUIDComponent.getComponentType());
            if (uuidComponent == null) {
                LOGGER.atSevere().log("Failed to spawn pet: UUIDComponent was null");
                return;
            }

            UUID entityID = uuidComponent.getUuid();
            TameComponent tameComponent = worldStore.getComponent(entityRef, TameComponent.getComponentType());
            if (tameComponent == null) {
                LOGGER.atSevere().log("Failed to spawn pet: Tame Component was null");
                return;
            }
            tameComponent.setIsTameByPlayer(playerId);
            Pet pet = new Pet(playerId, entityID, world.getName(), "Nib");
            NibletPlugin.getInstance().config.get().getPetsByPlayerId().put(String.valueOf(playerId), pet);
            NibletPlugin.getInstance().config.save();

            LOGGER.atInfo().log("Successfully spawned pet Niblet and set owner to " + playerId);

            NPCEntity npcEntity = spawnResult.second();
            Role role = npcEntity.getRole();
            if (role == null) {
                LOGGER.atSevere().log("Failed to spawn pet: Role for 'Niblet' was null");
                return;
            }

            role.getStateSupport().setState(entityRef, "Idle", "Follow", store);
            WorldSupport worldSupport = role.getWorldSupport();
            try {
                NibletPlugin.getAttitudeField().set(worldSupport, Attitude.REVERED);
            } catch (IllegalAccessException e) {
                LOGGER.atSevere().log("Failed to spawn pet: Failed to set attitude of NPC to REVERED", e);
            }
        });
    }

    private void onPlayerDisconnect(@Nonnull PlayerDisconnectEvent event) {
        PlayerRef playerRef = event.getPlayerRef();
        UUID playerId = playerRef.getUuid();
        Pet pet = config.get().getPetByPlayerId(playerId);

        if (pet == null) {
            LOGGER.atSevere().log("On Player Disconnect failed to get pet from PetHelper");
            return;
        }

        LOGGER.atInfo().log("On Player Disconnect: %s", pet.getName());
        UUID petEntityId = pet.getEntityId();
        String worldName = pet.getWorldName();

        Universe universe = Universe.get();
        if (universe == null) {
            return;
        }

        World world = worldName != null ? universe.getWorld(worldName) : null;
        if (world == null) {
            LOGGER.atSevere().log("On Player Disconnect failed to get world: %s. For pet: %s", worldName, pet.getName());
            return;
        }
        Ref<EntityStore> entityRef = world.getEntityStore().getRefFromUUID(petEntityId);

        world.execute(() -> {
            if (entityRef != null && entityRef.isValid()) {
                Store<EntityStore> store = world.getEntityStore().getStore();
                store.removeEntity(entityRef, RemoveReason.REMOVE);
                LOGGER.atInfo().log("On Player Disconnect successfully removed pet %s from the world", pet.getName());
            } else {
                LOGGER.atSevere().log("On Player Disconnect failed: entityRef was null or invalid for petEntityId: %s", petEntityId);
            }
        });
    }

    static {
        try {
            ATTITUDE_FIELD = WorldSupport.class.getDeclaredField("defaultPlayerAttitude");
            ATTITUDE_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access defaultPlayerAttitude", e);
        }
    }
}
