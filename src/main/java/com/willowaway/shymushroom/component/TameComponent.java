package com.willowaway.shymushroom.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.attitude.Attitude;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.willowaway.shymushroom.NibletPlugin;
import com.willowaway.shymushroom.model.Pet;
import com.willowaway.shymushroom.util.PetHelper;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TameComponent implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<TameComponent> CODEC = BuilderCodec.builder(TameComponent.class, TameComponent::new)
            .append(
                    new KeyedCodec<>("IsTame", Codec.BOOLEAN),
                    (tameComponent, isTame) -> tameComponent.isTame = isTame,
                    tameComponent -> tameComponent.isTame
            )
            .documentation("Is entity tamed").add()
            .append(
                    new KeyedCodec<>("TamedByPlayerId", Codec.UUID_BINARY),
                    (tameComponent, tamedByPlayerId) -> tameComponent.tamedByPlayerId = tamedByPlayerId,
                    tameComponent -> tameComponent.tamedByPlayerId
            )
            .documentation("The player id who tamed this entity").add()
            .build();

    private Boolean isTame = false;
    private UUID tamedByPlayerId = null;

    @Override
    public @Nullable Component<EntityStore> clone() {
        TameComponent tameComponent = new TameComponent();
        tameComponent.isTame = this.isTame;
        tameComponent.tamedByPlayerId = this.tamedByPlayerId;
        return tameComponent;
    }

    public static ComponentType<EntityStore, TameComponent> getComponentType() {
        return NibletPlugin.tameComponent;
    }

    public void setIsTameByPlayer(@Nonnull UUID playerId) {
        this.isTame = true;
        this.tamedByPlayerId = playerId;
    }

    public boolean isTame() {
        return Boolean.TRUE.equals(this.isTame);
    }

    public UUID getTamedByPlayerId() {
        return this.tamedByPlayerId;
    }
}
