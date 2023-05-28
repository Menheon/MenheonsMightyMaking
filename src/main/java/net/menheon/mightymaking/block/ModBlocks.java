package net.menheon.mightymaking.block;

import java.util.function.Supplier;
import net.menheon.mightymaking.MightyMaking;
import net.menheon.mightymaking.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
      MightyMaking.MOD_ID);

  public static final RegistryObject<Block> ENDER_SHARD_BLOCK = registerBlock("ender_shard_block",
      () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
          .strength(.8f).requiresCorrectToolForDrops()));

  private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
    RegistryObject<T> blockRegistry = BLOCKS.register(name, block);
    registerBlockItem(name, blockRegistry);
    return blockRegistry;
  }

  private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
    return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
  }

  public static void register(IEventBus eventBus) {
    BLOCKS.register(eventBus);
  }
}
