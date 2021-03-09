package org.armacraft.mod.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.block.GeneratorBlock;

public class ModItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArmaCraft.MODID);

    public static RegistryObject<BlockItem> GENERATOR_ITEM = ITEMS.register("generator",
            () -> new BlockItem(ModBlocks.GENERATOR.get(), new Item.Properties().maxStackSize(64).group(ItemGroup.BUILDING_BLOCKS)));

    public static RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS)));

    public static RegistryObject<Item> SILVER_MEDAL = ITEMS.register("silver_medal",
            () -> new Item(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC)));

    public static RegistryObject<Item> GOLD_MEDAL = ITEMS.register("gold_medal",
            () -> new Item(new Item.Properties().maxStackSize(64).group(ItemGroup.MISC)));
}
