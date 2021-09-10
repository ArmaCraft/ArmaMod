package org.armacraft.mod.bridge.bukkit;

import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.Set;
import java.util.UUID;

public interface IUserData {
    // @StringObfuscator:on
    enum Flags { NAMETAGS_SHOW_ALL, NAMETAGS_HIDE_ALL }
    // @StringObfuscator:off
    UUID getHolder();
    Set<String> getNametagWhitelist();
    Set<KeyBindWrapper> getKeyBinds();
    Set<Flags> getFlags();
    void setNametagWhitelist(Set<String> whitelist);
    void setKeyBinds(Set<KeyBindWrapper> binds);
    void toggleKeybindings(boolean enable);
    void setFlags(Set<Flags> flags);
    boolean hasBind(Character character);
    boolean areKeybindingsEnabled();
    void setRenderClothes(boolean bool);
    boolean renderClothes();
}
