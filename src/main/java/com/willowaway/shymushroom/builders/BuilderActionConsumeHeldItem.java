package com.willowaway.shymushroom.builders;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.validators.IntRangeValidator;
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase;
import com.hypixel.hytale.server.npc.instructions.Action;
import com.willowaway.shymushroom.actions.ActionConsumeHeldItem;

import javax.annotation.Nonnull;

public class BuilderActionConsumeHeldItem extends BuilderActionBase {

    @Override
    @Nonnull
    public Action build(@Nonnull BuilderSupport builderSupport) {
        return new ActionConsumeHeldItem(this, builderSupport);
    }

    public BuilderActionConsumeHeldItem readConfig(@Nonnull JsonElement data) {
        return this;
    }

    @Override
    @Nonnull
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.Stable;
    }

    @Override
    @Nonnull
    public String getShortDescription() {
        return "Consume held item";
    }

    @Override
    @Nonnull
    public String getLongDescription() {
        return this.getShortDescription();
    }
}
