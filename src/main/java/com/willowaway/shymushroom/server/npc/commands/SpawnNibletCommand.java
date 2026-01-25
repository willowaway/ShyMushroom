package com.willowaway.shymushroom.server.npc.commands;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import javax.annotation.Nonnull;
import java.awt.*;

public class SpawnNibletCommand extends AbstractPlayerCommand {

    public SpawnNibletCommand() {
        super("niblet", "Spawn Niblet");
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

            NPCEntity npcComponent = new NPCEntity();
            WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
            npcComponent.setSpawnInstant(worldTimeResource.getGameTime());
//            if (rotation == null) {
//                rotation = NULL_ROTATION;
//            }

            holder.addComponent(NPCEntity.getComponentType(), npcComponent);

            ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset("Niblet");
            Model model = Model.createScaledModel(modelAsset, 1.0f);

            TransformComponent transform = store.getComponent(playerRef.getReference(), EntityModule.get().getTransformComponentType());

            holder.addComponent(TransformComponent.getComponentType(), transform);
            holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(model.toReference()));
            holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
            holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(model.getBoundingBox()));
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));

            holder.ensureComponent(UUIDComponent.getComponentType());

            store.addEntity(holder, AddReason.SPAWN);
        });

        player.sendMessage(Message.raw("You have spawned a Niblet").color(Color.GREEN).bold(true));
    }
}
