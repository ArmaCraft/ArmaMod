package org.armacraft.mod.bridge.bukkit;

import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.Set;
import java.util.UUID;

public interface IUserData {
    // @StringObfuscator:on
    enum Flags { SHOW_ALL, HIDE_ALL }
    // @StringObfuscator:off
    UUID getHolder();
    Set<String> getNametagWhitelist();
    Set<KeyBindWrapper> getKeyBinds();
    Set<Flags> getFlags();
    void setNametagWhitelist(Set<String> whitelist);
    void setKeyBinds(Set<KeyBindWrapper> binds);
    void setFlags(Set<Flags> flags);
    boolean hasBind(Character character);
    boolean areKeybindsEnabled();
}
