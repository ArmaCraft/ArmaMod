package org.armacraft.mod.module;

public interface IModule {
    void load();
    String getId();
    ModuleSide getSide();
}
