package com.ncpbails.cookscollection.data;

import com.ncpbails.cookscollection.CooksCollection;
import com.ncpbails.cookscollection.block.ModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends ForgeAdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, existingFileHelper, List.of(new GeneralAdvancements()));
    }

    private static class GeneralAdvancements implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
            // Root advancement for obtaining the oven
            Advancement root = Advancement.Builder.advancement()
                    .display(
                            ModBlocks.OVEN.get(),
                            Component.translatable("advancement.cookscollection.root"),
                            Component.translatable("advancement.cookscollection.root.desc"),
                            new ResourceLocation("minecraft:textures/block/bricks.png"),
                            FrameType.TASK,
                            false,
                            false,
                            false
                    )
                    .addCriterion("has_oven", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(ModBlocks.OVEN.get()))
                    .save(consumer, String.valueOf(new ResourceLocation(CooksCollection.MOD_ID, "main/root")));

            // Advancement for placing the oven
            Advancement placeOven = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModBlocks.OVEN.get(),
                            Component.translatable("advancement.cookscollection.place_oven"),
                            Component.translatable("advancement.cookscollection.place_oven.desc"),
                            null,
                            FrameType.GOAL,
                            true,
                            true,
                            false
                    )
                    .addCriterion("oven", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(ModBlocks.OVEN.get()))
                    .save(consumer, String.valueOf(new ResourceLocation(CooksCollection.MOD_ID, "main/place_oven")));
        }
    }
}