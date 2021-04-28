package org.armacraft.mod.bridge.bukkit;

import java.util.Set;
import java.util.UUID;

public interface IUserData {
    enum Flags {
        SHOW_ALL, HIDE_ALL
    }

    static IUserData of(UUID holder, Set<Flags> flags, Set<String> whiteList) {
        return new IUserData() {
            @Override
            public UUID getHolder() {
                return holder;
            }

            @Override
            public Set<String> getNametagWhitelist() {
                return whiteList;
            }

            @Override
            public Set<Flags> getFlags() {
                return flags;
            }
        };
    }

    UUID getHolder();
    Set<String> getNametagWhitelist();
    Set<Flags> getFlags();
}
