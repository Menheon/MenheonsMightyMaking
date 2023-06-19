package net.menheon.mightymaking.item;

import net.menheon.mightymaking.MightyMaking;
import net.menheon.mightymaking.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
      MightyMaking.MOD_ID);

  public static final RegistryObject<Item> DUCK_FEATHER = initializeItemRegistryObject("duck_feather");
  public static final RegistryObject<Item> RAW_DUCK = initializeItemRegistryObject("raw_duck");
  public static final RegistryObject<Item> COOKED_DUCK = initializeItemRegistryObject("cooked_duck");
  public static final RegistryObject<Item> ENDER_SHARD = initializeItemRegistryObject("ender_shard");
  public static final RegistryObject<Item> ENDER_DIAMOND = initializeItemRegistryObject("ender_diamond");
  public static final RegistryObject<Item> CHARGED_ENDER_EYE = initializeItemRegistryObject("charged_ender_eye");
  public static final RegistryObject<Item> ENDER_HANDLE = initializeItemRegistryObject("ender_handle");
  public static final RegistryObject<Item> ENDER_SWORD = initializeItemRegistryObject("ender_sword");
  public static final RegistryObject<Item> WATER_ORB = initializeItemRegistryObject("water_orb");
  public static final RegistryObject<Item> FIRE_ORB = initializeItemRegistryObject("fire_orb");
  public static final RegistryObject<Item> EARTH_ORB = initializeItemRegistryObject("earth_orb");
  public static final RegistryObject<Item> AIR_ORB = initializeItemRegistryObject("air_orb");
  public static final RegistryObject<Item> ELEMENT_ORB = initializeItemRegistryObject("element_orb");
  public static final RegistryObject<Item> GOLD_STRING = initializeItemRegistryObject("gold_string");
  public static final RegistryObject<Item> HERMES_BOOTS = initializeItemRegistryObject("hermes_boots");
  public static final RegistryObject<Item> ENDER_GHOUL_SPAWN_EGG = ITEMS.register("ender_ghoul_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntities.ENDER_GHOUL, 000000, 4332651, new Properties()));

  private static final RegistryObject<Item> initializeItemRegistryObject(String name) {
    return ITEMS.register(name, () -> new Item(new Properties()));
  }

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
