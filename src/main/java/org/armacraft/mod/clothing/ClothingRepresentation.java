package org.armacraft.mod.clothing;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.craftingdead.core.capability.ModCapabilities;
import com.craftingdead.core.item.ClothingItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ClothingRepresentation {
    private static final Map<ClothingItem, ProtectionLevel> REGISTERED_CLOTHING = new HashMap<>();

    private ProtectionLevel protectionLevel;
    private ClothingItem clothing;

    public ClothingRepresentation(ProtectionLevel protectionLevel, ClothingItem clothing) {
        this.protectionLevel = protectionLevel;
        this.clothing = clothing;
    }
    
    public static void register(Item clothingItem, ProtectionLevel level) {
    	REGISTERED_CLOTHING.put((ClothingItem) clothingItem, level);
    }
    
    public static boolean has(Item item) {
    	return REGISTERED_CLOTHING.containsKey(item);
    }
    
    public static ProtectionLevel getLevel(Item item) {
    	return REGISTERED_CLOTHING.get(item);
    }

    public static Optional<ClothingRepresentation> from(ItemStack stack) {
        AtomicReference<Optional<ClothingRepresentation>> atomicClothingRepresentation = new AtomicReference<>(Optional.empty());
        stack.getCapability(ModCapabilities.CLOTHING).ifPresent(clothingController -> {
            REGISTERED_CLOTHING.entrySet().stream()
                    .filter(entry -> entry.getKey().equals(stack.getItem()))
                    .findFirst().ifPresent(entry ->
                    atomicClothingRepresentation.set(Optional.of(new ClothingRepresentation(entry.getValue(), (ClothingItem) stack.getItem()))));

        });
        return atomicClothingRepresentation.get();
    }

    public ProtectionLevel getProtectionLevel() {
        return this.protectionLevel;
    }

    public ClothingItem getClothing() {
        return this.clothing;
    }
}

