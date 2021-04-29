package org.armacraft.mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.Set;
import java.util.UUID;

public class ClientUserData implements IUserData {
    private Set<KeyBindWrapper> keybinds;
    private Set<Flags> flags;
    private Set<String> nametagWhitelist;

    public ClientUserData(Set<KeyBindWrapper> keybinds, Set<Flags> flags, Set<String> nametagWhitelist) {
        this.keybinds = keybinds;
        this.flags = flags;
        this.nametagWhitelist = nametagWhitelist;
    }

    @Override
    public UUID getHolder() {
        return Minecraft.getInstance().player.getUUID();
    }

    @Override
    public Set<String> getNametagWhitelist() {
        return nametagWhitelist;
    }

    @Override
    public Set<KeyBindWrapper> getKeyBinds() {
        return keybinds;
    }

    @Override
    public Set<Flags> getFlags() {
        return flags;
    }

    @Override
    public void setNametagWhitelist(Set<String> whitelist) {
        this.nametagWhitelist = whitelist;
    }

    @Override
    public void setKeyBinds(Set<KeyBindWrapper> binds) {
        this.keybinds = binds;
    }

    @Override
    public void setFlags(Set<Flags> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasBind(Character character) {
        return keybinds.stream()
                .anyMatch(keybind -> keybind.getBind().toString().equalsIgnoreCase(character.toString()));
    }

    @Override
    public boolean hasBind(KeyBinding bind) {
        return hasBind((char) bind.getKey().getValue());
    }
}
