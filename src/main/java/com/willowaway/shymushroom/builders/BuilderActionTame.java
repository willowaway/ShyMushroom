package com.willowaway.shymushroom.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringArrayHolder;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.willowaway.shymushroom.actions.ActionTame;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class BuilderActionTame extends BuilderActionBase {
    protected StringArrayHolder tameItemsHolder = new StringArrayHolder();

    @Override
    public @Nullable String getShortDescription() {
        return "Tame a NPC or entity";
    }

    @Override
    public @Nullable String getLongDescription() {
        return this.getShortDescription();
    }

    @Override
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionTame(this, builderSupport);
    }

    @Override
    public @Nullable BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    public Builder<Action> readConfig(@Nonnull JsonElement data) {
        this.requireStringArray(data, "TameItems", this.tameItemsHolder, 1, Integer.MAX_VALUE, null,
                BuilderDescriptorState.Stable, "The tame items", "The tame items the NPC or entity will be interested for taming them");
        return super.readConfig(data);
    }

    public String[] getTameItems(@Nonnull BuilderSupport support) {
        return this.tameItemsHolder.get(support.getExecutionContext());
    }
}
