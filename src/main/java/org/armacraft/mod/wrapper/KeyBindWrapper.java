package org.armacraft.mod.wrapper;

public class KeyBindWrapper {
    private Character bind;
    private String command;

    public KeyBindWrapper(Character bind, String command) {
        this.bind = bind;
        this.command = command;
    }

    public Character getBind() {
        return bind;
    }

    public String getCommand() {
        return command;
    }

    public static KeyBindWrapper fromString(String str) {
        return new KeyBindWrapper(str.split("%")[0].charAt(0), str.split("%")[1]);
    }

    @Override
    public String toString() {
        return bind.toString() + ";" + command;
    }
}
