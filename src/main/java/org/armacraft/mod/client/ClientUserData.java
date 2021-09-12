package org.armacraft.mod.client;

import net.minecraft.client.Minecraft;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientUserData implements IUserData {
    private Set<Flags> flags = new HashSet<>();
    private Set<KeyBindWrapper> binds = new HashSet<>();
    private Set<String> nametagWhitelist = new HashSet<>();
    private boolean areKeybindsEnabled = true;
    private boolean renderClothes = false;

    public ClientUserData(Set<Flags> flags, Set<String> nametagWhitelist, Set<KeyBindWrapper> binds, boolean areKeybindsEnabled, boolean renderClothes) {
        this.areKeybindsEnabled = areKeybindsEnabled;
        this.binds = binds;
        this.flags = flags;
        this.nametagWhitelist = nametagWhitelist;
        this.renderClothes = renderClothes;
    }

    public ClientUserData() {}

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
        return binds;
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
        this.binds = binds;
    }

    @Override
    public void toggleKeybindings(boolean enable) {
        this.areKeybindsEnabled = enable;
    }

    @Override
    public void setFlags(Set<Flags> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasBind(Character character) { return binds.stream().anyMatch(x -> x.getBind().equals(character)); }

    @Override
    public boolean areKeybindingsEnabled() {
        return areKeybindsEnabled;
    }

    @Override
    public void setRenderClothes(boolean bool) {
        this.renderClothes = bool;
    }

    @Override
    public boolean renderClothes() {
        return renderClothes;
    }
}
