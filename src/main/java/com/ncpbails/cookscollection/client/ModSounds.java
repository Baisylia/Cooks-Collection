package com.ncpbails.cookscollection.client;

import com.ncpbails.cookscollection.CooksCollection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.ncpbails.cookscollection.CooksCollection.MOD_ID;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> OVEN_CRACKLE = SOUND_EVENTS.register("oven_crackle",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "block.oven.crackle")));

    public static final RegistryObject<SoundEvent> OVEN_OPEN = SOUND_EVENTS.register("oven_open",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "block.oven.open")));

    public static final RegistryObject<SoundEvent> OVEN_CLOSE = SOUND_EVENTS.register("oven_close",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "block.oven.close")));

    public static final RegistryObject<SoundEvent> LEAVES_PICKED = SOUND_EVENTS.register("fruiting_leaves_pick_fruit",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "block.fruiting_leaves.pick_fruit")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}