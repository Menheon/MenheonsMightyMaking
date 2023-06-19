package net.menheon.mightymaking.sound;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.resources.ResourceLocation;
import net.menheon.mightymaking.MightyMaking;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

  public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister
      .create(ForgeRegistries.SOUND_EVENTS, MightyMaking.MOD_ID);

  private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
    ResourceLocation id = new ResourceLocation(MightyMaking.MOD_ID, name);
    return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
  }

  public static void register(IEventBus eventBus) {
    SOUND_EVENTS.register(eventBus);
  }

  public static final RegistryObject<SoundEvent> ENDER_GHOUL_HURT = registerSoundEvent("ender_ghoul_hurt");
  public static final RegistryObject<SoundEvent> ENDER_GHOUL_DEATH = registerSoundEvent("ender_ghoul_death");
  public static final RegistryObject<SoundEvent> ENDER_GHOUL_AMBIENT = registerSoundEvent("ender_ghoul_ambient");
  public static final RegistryObject<SoundEvent> ENDER_GHOUL_STARE = registerSoundEvent("ender_ghoul_stare");
}

