package net.menheon.mightymaking;

import net.menheon.mightymaking.block.ModBlocks;
import net.menheon.mightymaking.item.ModCreativeModeTab;
import net.menheon.mightymaking.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MightyMaking.MOD_ID)
public class MightyMaking {
  public static final String MOD_ID = "mightymaking";

  public MightyMaking() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ModItems.register(modEventBus);
    ModBlocks.register(modEventBus);

    // Register the commonSetup method for modloading
    modEventBus.addListener(this::commonSetup);

    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);

    // Register the item to a creative tab
    modEventBus.addListener(this::addCreative);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
  }

  private void addCreative(CreativeModeTabEvent.BuildContents event) {
    if (event.getTab() == ModCreativeModeTab.MIGHTY_MAKING_TAB) {
      ModItems.ITEMS.getEntries().forEach(item -> {
        event.accept(item);
      });
      ModBlocks.BLOCKS.getEntries().forEach(block -> {
        event.accept(block);
      });
    }
  }

  // You can use EventBusSubscriber to automatically register all static methods
  // in the class annotated with @SubscribeEvent
  @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
    }
  }
}
