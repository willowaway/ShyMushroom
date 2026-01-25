package com.willowaway.shymushroom.actions;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.corecomponents.ActionBase;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider;
import com.willowaway.shymushroom.builders.BuilderActionConsumeHeldItem;

import javax.annotation.Nonnull;

public class ActionConsumeHeldItem  extends ActionBase {

    public ActionConsumeHeldItem(@Nonnull BuilderActionConsumeHeldItem builderAction,  @Nonnull BuilderSupport builderSupport) {
        super(builderAction);
    }

    public boolean execute(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, InfoProvider sensorInfo, double dt, @Nonnull Store<EntityStore> store) {
        super.execute(ref, role, sensorInfo, dt, store);
        Ref<EntityStore> refStore = role.getStateSupport().getInteractionIterationTarget();
        if (refStore == null) {
            return false;
        }

        Player player = store.getComponent(refStore, Player.getComponentType());
        if (player == null) {
            return false;
        }

        UUIDComponent playerIdComp = store.getComponent(refStore, UUIDComponent.getComponentType());
        if (playerIdComp == null) {
            return false;
        }

        Inventory inventory = player.getInventory();
        if (inventory == null) {
            return false;
        }

        byte slot = inventory.getActiveHotbarSlot();
        ItemStack itemStack = inventory.getHotbar().getItemStack(slot);
        if (itemStack == null) {
            return false;
        }

        inventory.getHotbar().removeItemStackFromSlot(slot, 1);
        player.sendInventory();
        return true;
    }
}
