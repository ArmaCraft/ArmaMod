package org.armacraft.mod.mixin;

import de.maxhenkel.corpse.corelib.inventory.ItemListInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemListInventory.class)
public abstract class ItemListInventoryMixin implements IInventory {
    @Shadow protected NonNullList<ItemStack> items;

    @Override
    public ItemStack getItem(int index) {
        if(index < items.size()) {
            return this.items.get(index);
        }
        return ItemStack.EMPTY;
    }
}
