package com.willowaway.shymushroom.model;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Pet {
    public static final BuilderCodec<Pet> CODEC = BuilderCodec.builder(Pet.class, Pet::new)
            .append(new KeyedCodec<>("Id", Codec.UUID_STRING),
                    (pet, value) -> pet.id = value, (pet) -> pet.id)
            .documentation("The Pet Id").add()
            .append(new KeyedCodec<>("EntityId", Codec.UUID_STRING),
                    (pet, value) -> pet.entityId = value, (pet) -> pet.entityId)
            .documentation("The Entity Id").add()
            .append(new KeyedCodec<>("PlayerId", Codec.UUID_STRING),
                    (pet, value) -> pet.playerId = value, (pet) -> pet.playerId)
            .documentation("The Player Id who owns the Pet").add()
            .append(new KeyedCodec<>("Name", Codec.STRING),
                    (pet, value) -> pet.name = value, (pet) -> pet.name)
            .documentation("The Pet Name").add()
            .append(new KeyedCodec<>("WorldName", Codec.STRING),
                    (pet, value) -> pet.worldName = value, (pet) -> pet.worldName)
            .documentation("The World Name the pet belongs to").add()
            .build();

    private UUID id = UUID.randomUUID();
    private UUID entityId;
    private UUID playerId;
    private String name;
    private String worldName;

    public Pet() {}

    public Pet(UUID playerId, UUID entityId, String worldName, String name) {
        this.playerId = playerId;
        this.entityId = entityId;
        this.worldName = worldName;
        this.name = name != null ? name : "My Pet";
    }
}
