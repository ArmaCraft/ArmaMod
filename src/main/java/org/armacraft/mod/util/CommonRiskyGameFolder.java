package org.armacraft.mod.util;

import java.util.List;

import org.armacraft.mod.client.ClientRiskyGameFolder;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CommonRiskyGameFolder implements RiskyGameFolder {
	
	private final String folderPath;
	private final String regex;
	
	public CommonRiskyGameFolder(String folderPath, String regex) {
		// @StringObfuscator:on
		this.regex = regex;
		this.folderPath = folderPath;
		// @StringObfuscator:off
	}

	@Override
	public String getFolderPath() {
		return this.folderPath;
	}

	@Override
	public String getFinderRegex() {
		return this.regex;
	}
	
	@OnlyIn(Dist.CLIENT)
	public ClientRiskyGameFolder asClient() {
		return new ClientRiskyGameFolder(this.folderPath, this.regex);
	}
	
	public static List<CommonRiskyGameFolder> defaults() {
		// @StringObfuscator:on
		return ImmutableList.copyOf(new CommonRiskyGameFolder[] {
				new CommonRiskyGameFolder("./coremods", ".*"),
				new CommonRiskyGameFolder("./mods", ".*"),
				new CommonRiskyGameFolder("./bin", "(zip|jar)$"),
				new CommonRiskyGameFolder("./resourcepacks", ".*"),
				new CommonRiskyGameFolder("./shaderpacks", ".*")
		});
		// @StringObfuscator:off
	}
	
	@Override
	public void write(PacketBuffer out) {
		out.writeUtf(this.getFolderPath());
		out.writeUtf(this.getFinderRegex());
	}
	
	public static CommonRiskyGameFolder read(PacketBuffer in) {
		String folderPath = in.readUtf(200);
		String regex = in.readUtf(15);
		return new CommonRiskyGameFolder(folderPath, regex);
	}
}
