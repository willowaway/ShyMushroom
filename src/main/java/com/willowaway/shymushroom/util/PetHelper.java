package com.willowaway.shymushroom.util;

import com.hypixel.hytale.logger.HytaleLogger;
import com.willowaway.shymushroom.NibletPlugin;
import com.willowaway.shymushroom.config.PetConfig;
import com.willowaway.shymushroom.model.Pet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetHelper {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final Map<UUID, Pet> PETS_BY_PLAYER_ID = new HashMap<>();

    public static void setup(PetConfig config) {
        PETS_BY_PLAYER_ID.clear();
        Map<String, Pet> petsInConfig = config.getPetsByPlayerId();
        if (petsInConfig != null) {
            PETS_BY_PLAYER_ID.putAll(convertTo(petsInConfig));
            LOGGER.atInfo().log("Pets by Player Id loaded with %s entries.", PETS_BY_PLAYER_ID.size());
        } else {
            LOGGER.atSevere().log("Failed to load Pets by Player Id: Pets in config were null");
        }
    }

    public static Map<UUID, Pet> convertTo(Map<String, Pet> sourceMap) {
        Map<UUID, Pet> destinationMap = new HashMap<>();

        for (Map.Entry<String, Pet> entry : sourceMap.entrySet()) {
            try {
                UUID keyUuid = UUID.fromString(entry.getKey());
                Pet pet = entry.getValue();
                destinationMap.put(keyUuid, pet);
            } catch (IllegalArgumentException e) {
                LOGGER.atSevere().log("Failed to convert Map<String, Pet> to Map<UUID, Pet>");
            }
        }
        return destinationMap;
    }

    public static Pet getPetByPlayerId(UUID playerId) {
        return PETS_BY_PLAYER_ID.get(playerId);
    }
}
