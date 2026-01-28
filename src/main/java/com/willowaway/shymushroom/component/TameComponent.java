package com.willowaway.shymushroom.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.willowaway.shymushroom.NibletPlugin;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TameComponent implements Component<EntityStore> {

    // CODECs are a mechanism for serializing and deserializing data to and from JSON
    // CODECS define how data is structured
    @Nonnull
    public static final BuilderCodec<TameComponent> CODEC = BuilderCodec.builder(TameComponent.class, TameComponent::new)
            .append(
                    new KeyedCodec<>("IsTame", Codec.BOOLEAN),
                    (tameComponent, isTame) -> tameComponent.isTame = isTame,
                    tameComponent -> tameComponent.isTame
            )
            .documentation("Is entity tamed").add()
            .build();

    private Boolean isTame = false;

    @Override
    public Component<EntityStore> clone() {
        TameComponent tameComponent = new TameComponent();
        tameComponent.isTame = this.isTame;
        return tameComponent;
    }

    public static ComponentType<EntityStore, TameComponent> getComponentType() {
        return NibletPlugin.tameComponent;
    }

    public void setIsTameByPlayer(@Nonnull UUID playerId) {
        this.isTame = true;
    }

    public boolean isTame() {
        return Boolean.TRUE.equals(this.isTame);
    }
}
