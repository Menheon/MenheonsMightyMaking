package net.menheon.mightymaking.event;

import net.menheon.mightymaking.MightyMaking;
import net.menheon.mightymaking.entity.ModEntities;
import net.menheon.mightymaking.entity.custom.EnderGhoulEntity;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MightyMaking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

  @SubscribeEvent
  public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
    event.put(ModEntities.ENDER_GHOUL.get(), EnderGhoulEntity.setAttributes());
  }

  @SubscribeEvent
  public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
    event.register(
        ModEntities.ENDER_GHOUL.get(),
        SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        Monster::checkMonsterSpawnRules,
        SpawnPlacementRegisterEvent.Operation.REPLACE);
  }
}
