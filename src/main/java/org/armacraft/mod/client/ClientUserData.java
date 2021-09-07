package org.armacraft.mod.client;

import net.minecraft.client.Minecraft;
import org.armacraft.mod.bridge.bukkit.IUserData;
import org.armacraft.mod.wrapper.KeyBindWrapper;

import java.util.Set;
import java.util.UUID;

public class ClientUserData implements IUserData {
    private Set<Flags> flags;
    private Set<String> nametagWhitelist;
    private boolean areKeybindsEnabled;
    private boolean renderClothes;

    public ClientUserData(Set<Flags> flags, Set<String> nametagWhitelist, boolean areKeybindsEnabled, boolean renderClothes) {
        this.areKeybindsEnabled = areKeybindsEnabled;
        this.flags = flags;
        this.nametagWhitelist = nametagWhitelist;
        this.renderClothes = renderClothes;
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
        return null;
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

    }

    @Override
    public void setFlags(Set<Flags> flags) {
        this.flags = flags;
    }

    @Override
    public boolean hasBind(Character character) { return false; }

    @Override
    public boolean areKeybindsEnabled() {
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
