package org.armacraft.mod.init;

import org.armacraft.mod.ArmaCraft;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArmaCraftItems {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ArmaCraft.MODID);

    public static RegistryObject<BlockItem> GENERATOR_ITEM = ITEMS.register("generator",
            () -> new BlockItem(ArmaCraftBlocks.GENERATOR.get(), new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_BUILDING_BLOCKS)));

    public static RegistryObject<Item> SCREWDRIVER = ITEMS.register("screwdriver",
            () -> new Item(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_TOOLS)));

    public static RegistryObject<Item> SILVER_MEDAL = ITEMS.register("silver_medal",
            () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MISC)));

    public static RegistryObject<Item> GOLD_MEDAL = ITEMS.register("gold_medal",
            () -> new Item(new Item.Properties().stacksTo(64).tab(ItemGroup.TAB_MISC)));
}
