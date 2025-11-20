package com.baisylia.cookscollection.block.entity;

import com.baisylia.cookscollection.CooksCollection;
import com.baisylia.cookscollection.block.ModBlocks;
import com.baisylia.cookscollection.block.entity.custom.OvenBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CooksCollection.MOD_ID);

    public static final RegistryObject<BlockEntityType<OvenBlockEntity>> OVEN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("oven_block_entity", () ->
                    BlockEntityType.Builder.of(OvenBlockEntity::new,
                            ModBlocks.OVEN.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}