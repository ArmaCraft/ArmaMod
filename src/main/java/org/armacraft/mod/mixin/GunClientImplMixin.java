package org.armacraft.mod.mixin;

import java.util.HashMap;

import org.armacraft.mod.bridge.IGunImplBridge;
import org.armacraft.mod.util.GunUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.craftingdead.core.capability.gun.GunClientImpl;
import com.craftingdead.core.capability.gun.GunImpl;
import com.craftingdead.core.capability.gun.PendingHit;
import com.craftingdead.core.capability.living.ILiving;
import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.item.AttachmentItem.MultiplierType;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.ValidatePendingHitMessage;
import com.google.common.collect.Multimap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

@Mixin(GunClientImpl.class)
public abstract class GunClientImplMixin {

	@Shadow()
	@Final()
	private ClientDist client;

	@Shadow()
	@Final()
	private Minecraft minecraft;

	@Shadow()
	@Final()
	private GunImpl gun;
	
	@Shadow()
	@Final()
	private Multimap<Integer, PendingHit> livingHitValidationBuffer;

	/**
	 * Faz com que seja aplicado o jolt contendo o spread original da arma caso
	 * estiver mirando, já que pro Crafting Dead, não existe mais recoil, e sim
	 * accuracy. A accuracy enquanto mira é sempre mínima.
	 */
	@Inject(method = "handleShoot", remap = false, at = @At("HEAD"))
	public void handleShoot(ILiving<?, ?> living, CallbackInfo ci) {
		LivingEntity theEntity = living.getEntity();
		if (GunUtils.isAiming(theEntity)) {
			if (theEntity == this.minecraft.getCameraEntity()) {
				this.client.getCameraManager()
						.joltCamera(1.0F - ((IGunImplBridge) this.gun).bridge$getGunProvider().getAccuracyPct()
								* gun.getAttachmentMultiplier(MultiplierType.ACCURACY), true);
			}
		}
	}

	/**
	 * Faz com que os pacotes de tiro sejam enviados diretamente ao servidor ao inves de ficarem em um buffer,
	 * pois os tiros estavam sofrendo um merge de dano. Isso, no CDA, é pra salvar desempenho. Mas aqui, simplesmente
	 * ignoramos isso.
	 */
	@Inject(method = "handleHitEntityPre", remap = false, at = @At("TAIL"))
	public void handleHitEntityPre(ILiving<?, ?> living, Entity hitEntity, Vector3d hitPos, long randomSeed,
			CallbackInfo ci) {
		NetworkChannel.PLAY.getSimpleChannel()
				.sendToServer(new ValidatePendingHitMessage(new HashMap<>(this.livingHitValidationBuffer.asMap())));
		
		// Por ser limpo aqui, o CDA entende que não tem nada pra enviar, então por isso NA TEORIA n�o acaba enviando mais de uma vez
		this.livingHitValidationBuffer.clear();
	}
}
