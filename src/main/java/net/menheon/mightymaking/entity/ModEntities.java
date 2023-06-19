package net.menheon.mightymaking.entity;

import net.menheon.mightymaking.MightyMaking;
import net.menheon.mightymaking.entity.custom.EnderGhoulEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
      .create(ForgeRegistries.ENTITY_TYPES, MightyMaking.MOD_ID);

  public static final RegistryObject<EntityType<EnderGhoulEntity>> ENDER_GHOUL = ENTITY_TYPES.register("ender_ghoul",
      () -> EntityType.Builder.of(EnderGhoulEntity::new, MobCategory.CREATURE)
          .sized(1.95F, 3.15F)
          .build(new ResourceLocation(MightyMaking.MOD_ID, "ender_ghoul")
              .toString()));

  public static void register(IEventBus eventBus) {
    ENTITY_TYPES.register(eventBus);
  }
}
