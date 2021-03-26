package org.armacraft.acbasics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.armacraft.acbasics.module.IModule;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ScreenCapturer implements IModule {

    @Override
    public void load() {
        //TODO: SETUP PACKET LISTENER
    }

    private File screenshot() throws IOException {
        int width = Minecraft.getInstance().getMainWindow().getFramebufferWidth();
        int height = Minecraft.getInstance().getMainWindow().getFramebufferHeight();
        Framebuffer buffer = Minecraft.getInstance().getFramebuffer();
        File folder = new File(Minecraft.getInstance().gameDir, "ac");
        folder.mkdir();
        File screenshot = new File(folder, UUID.randomUUID().toString());
        ScreenShotHelper.createScreenshot(width, height, buffer).write(screenshot);
        return screenshot;
    }
}
