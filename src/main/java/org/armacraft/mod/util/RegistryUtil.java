package org.armacraft.mod.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryUtil {
    public static <G extends IForgeRegistryEntry<? super G>> Collection<RegistryObject<G>> filterRegistries(Class<G> clazz, DeferredRegister<?> register) {
        return register.getEntries().stream()
                .filter(entry -> clazz.isInstance(entry.get()))
                .map(entry -> (RegistryObject<G>) entry)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static Optional<RegistryObject<?>> filterRegistries(String id, DeferredRegister<?> register) {
        return Optional.of( register.getEntries().stream().filter(entry -> entry.getId().toString().equalsIgnoreCase(id)).findFirst().get());
    }

}
