package org.armacraft.mod.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.environment.EnvironmentWrapper;
import org.armacraft.mod.environment.ProcessWrapper;
import org.armacraft.mod.util.Cooldown;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientEnvironmentResponsePacket {
    private EnvironmentWrapper wrapper;

    private ClientEnvironmentResponsePacket(EnvironmentWrapper environment) {
        wrapper = environment;
    }

    public static void encode(ClientEnvironmentResponsePacket msg, PacketBuffer out) {
        // @StringObfuscator:on
        EnvironmentWrapper environmentWrapper = ArmaCraft.getInstance().getClientDist().get().getEnvironment();
        out.writeByteArray(environmentWrapper.getOperationalSystem().getBytes());
        out.writeByteArray(environmentWrapper.getJavaVersion().getBytes());
        out.writeInt(environmentWrapper.getRunningProcesses().size());
        environmentWrapper.getRunningProcesses()
                .forEach(processWrapper -> out.writeByteArray(processWrapper.toString().getBytes()));
        // @StringObfuscator:off
    }

    public static ClientEnvironmentResponsePacket decode(PacketBuffer in) {
        // @StringObfuscator:on
        String os = in.readUtf();
        String java = in.readUtf();
        int processSize = in.readInt();
        Set<ProcessWrapper> runningProcesses = new HashSet<>();

        for(int i = 0; i < processSize; i++) {
            runningProcesses.add(ProcessWrapper.fromSimpleString(in.readUtf()));
        }

        EnvironmentWrapper environmentWrapper = new EnvironmentWrapper(os, java, runningProcesses);
        // @StringObfuscator:off

        return new ClientEnvironmentResponsePacket(environmentWrapper);
    }

    public static boolean handle(ClientEnvironmentResponsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isServer()) {
            return true;
        }
        
        if (Cooldown.checkAndPut(ctx.get().getSender(), "environmentresponse", 300)) {
        	// Está em cooldown
        	return true;
        }

        ArmaCraft.getInstance().getServerDist().ifPresent(dist ->
            dist.getForgeToBukkitInterface().onEnvironmentReceive(ctx.get().getSender(), msg.getEnvironment()));

        return true;
    }

    public EnvironmentWrapper getEnvironment() {
        return wrapper;
    }

    public static ClientEnvironmentResponsePacket empty() {
        return new ClientEnvironmentResponsePacket(null);
    }
}
