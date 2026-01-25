package com.willowaway.shymushroom.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.willowaway.shymushroom.model.Pet;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PetConfig {
    public static final BuilderCodec<PetConfig> CODEC = BuilderCodec.builder(PetConfig.class, PetConfig::new)
            .append(
                    new KeyedCodec<>("PetsByPlayerId",
                            new MapCodec<>(Pet.CODEC, HashMap::new)),
                    (config, map) -> config.petsByPlayerId = map,
                    config -> config.petsByPlayerId
            ).add()
            .build();

    private Map<String, Pet> petsByPlayerId = new HashMap<>();

    public Pet getPetByPlayerId(UUID playerId) {
        return petsByPlayerId.get(String.valueOf(playerId));
    }

    public void setPetByPlayerId(String playerId, Pet pet) {
        petsByPlayerId.put(playerId, pet);
    }
}