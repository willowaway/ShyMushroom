package com.willowaway.shymushroom.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.willowaway.shymushroom.NibletPlugin;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.Random;

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
            .append(
                    new KeyedCodec<>("TameChancePercentage", Codec.INTEGER),
                    (tameComponent, chanceToTame) -> tameComponent.tameChancePercentage = chanceToTame,
                    tameComponent -> tameComponent.tameChancePercentage
            )
            .documentation("Is entity tamed").add()
            .build();

    @Setter
    @Getter
    private Boolean isTame = false;

    @Getter
    private Integer tameChancePercentage = 0;

    @Override
    public Component<EntityStore> clone() {
        TameComponent tameComponent = new TameComponent();
        tameComponent.isTame = this.isTame;
        tameComponent.tameChancePercentage = this.tameChancePercentage;
        return tameComponent;
    }

    public boolean attemptToTame() {
        Random rand = new Random();
        int roll = rand.nextInt(100) + 1;

        if (roll <= tameChancePercentage) {
            isTame = true;
        } else {
            tameChancePercentage += 10;
        }

        return isTame;
    }

    public static ComponentType<EntityStore, TameComponent> getComponentType() {
        return NibletPlugin.tameComponent;
    }
}
