package org.armacraft.mod.wrapper;

public class ResourceLocationWrapper {
    private String path;
    private String namespace;

    public ResourceLocationWrapper(String path, String namespace) {
        this.path = path;
        this.namespace = namespace;
    }

    public static ResourceLocationWrapper of(String resourceLocation) {
        return new ResourceLocationWrapper(resourceLocation.split(":")[0], resourceLocation.split(":")[1]);
    }

    public String getPath() {
        return path;
    }

    public String getNamespace() {
        return namespace;
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}
