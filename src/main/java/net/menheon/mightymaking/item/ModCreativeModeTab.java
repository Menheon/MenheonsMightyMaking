package net.menheon.mightymaking.item;

import net.menheon.mightymaking.MightyMaking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MightyMaking.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTab {
  public static CreativeModeTab MIGHTY_MAKING_TAB;

  @SubscribeEvent
  public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
    MIGHTY_MAKING_TAB = event.registerCreativeModeTab(
        new ResourceLocation(MightyMaking.MOD_ID, "mighty_making_tab"),
        builder -> builder.icon(() -> new ItemStack(ModItems.ELEMENT_ORB.get()))
            .title(Component.translatable("creativemodetab.mighty_making_tab")));
  }
}
