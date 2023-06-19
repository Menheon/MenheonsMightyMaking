package net.menheon.mightymaking.event;

import net.menheon.mightymaking.MightyMaking;
import net.menheon.mightymaking.entity.ModEntities;
import net.menheon.mightymaking.entity.custom.EnderGhoulEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MightyMaking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

  @SubscribeEvent
  public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
    event.put(ModEntities.ENDER_GHOUL.get(), EnderGhoulEntity.setAttributes());
  }
}
