package org.armacraft.mod.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.environment.EnvironmentWrapper;
import org.armacraft.mod.environment.ProcessWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.function.Supplier;

public class ClientClassesHashResponsePacket {
    private String classesHash;

    public ClientClassesHashResponsePacket(String classesHash) {
        this.classesHash = classesHash;
    }

    public static void encode(ClientClassesHashResponsePacket msg, PacketBuffer out) {
        byte messageDigest[] = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Field classesF = ClassLoader.class.getDeclaredField("classes");
            classesF.setAccessible(true);
            Vector<Class<?>> classes = (Vector<Class<?>>) classesF.get(loader);
            StringBuilder builder = new StringBuilder();
            classes.forEach(clazz -> builder.append(clazz.getCanonicalName() + ":"));
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            messageDigest = algorithm.digest(builder.toString().getBytes("UTF-8"));
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        out.writeByteArray(messageDigest);
    }

    public static ClientClassesHashResponsePacket decode(PacketBuffer in) {
        return new ClientClassesHashResponsePacket(in.readUtf());
    }

    public static boolean handle(ClientClassesHashResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }

        ArmaCraft.getInstance().getServerDist().ifPresent(dist -> {
            if(!dist.validateClassesHash(msg.classesHash, ctx.get().getSender())) {
                ctx.get().enqueueWork(() -> {
                    ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(), new CloseGamePacket());
                });
            }
        });

        return true;
    }
}
