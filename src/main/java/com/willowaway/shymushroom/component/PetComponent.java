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
import javax.annotation.Nullable;
import java.util.UUID;

@Getter
public class PetComponent implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<PetComponent> CODEC = BuilderCodec.builder(PetComponent.class, PetComponent::new)
            .append(
                    new KeyedCodec<>("EntityId", Codec.UUID_BINARY),
                    (pet, id) -> pet.entityId = id,
                    petComponent -> petComponent.entityId
            )
            .documentation("The pet entity id").add()
            .append(new KeyedCodec<>("Name", Codec.STRING),
                    (pet, value) -> pet.name = value, (pet) -> pet.name)
            .documentation("Name of the pet").add()
            .append(new KeyedCodec<>("WorldName", Codec.STRING),
                    (pet, value) -> pet.worldName = value, (pet) -> pet.worldName)
            .documentation("The World Name the pet belongs to").add()
            .build();

    @Setter
    @Nullable
    private UUID entityId;
    private String name;
    private String worldName;

    @Override
    public Component<EntityStore> clone() {
        PetComponent petComponent = new PetComponent();
        petComponent.entityId = this.entityId;
        petComponent.name = this.name;
        petComponent.worldName = this.worldName;
        return petComponent;
    }

    public void set(@Nonnull UUID entityId, @Nonnull String worldName, String name) {
        this.entityId = entityId;
        this.worldName = worldName;
        this.name = name != null ? name : "Nib";
    }

    public static ComponentType<EntityStore, PetComponent> getComponentType() {
        return NibletPlugin.petComponent;
    }
}
