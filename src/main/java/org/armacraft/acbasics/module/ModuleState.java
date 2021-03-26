package org.armacraft.acbasics.module;

import java.util.Arrays;

public enum ModuleState {
    ENABLED(1), DISABLED(0);

    private int id;

    ModuleState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static ModuleState by(int id) {
        return Arrays.asList(values()).stream().filter(x -> x.getId() == id).findFirst().get();
    }
}
