package org.armacraft.mod.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.armacraft.mod.ArmaCraft;

public class ModItems {
    public static Item screwdriver;
    public static BlockItem generatorBlockItem;

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        generatorBlockItem = new BlockItem(ModBlocks.generatorBlock, new Item.Properties().maxStackSize(64).group(ItemGroup.BUILDING_BLOCKS));
        generatorBlockItem.setRegistryName(ModBlocks.generatorBlock.getRegistryName());

        screwdriver = new Item(new Item.Properties().maxStackSize(1).group(ItemGroup.TOOLS));
        screwdriver.setRegistryName(ArmaCraft.MODID, "screwdriver");
        itemRegisterEvent.getRegistry().register(generatorBlockItem);
        itemRegisterEvent.getRegistry().register(screwdriver);
    }
}
