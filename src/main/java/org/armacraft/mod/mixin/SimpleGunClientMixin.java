package org.armacraft.mod.mixin;

import com.craftingdead.core.client.ClientDist;
import com.craftingdead.core.item.AttachmentItem.MultiplierType;
import com.craftingdead.core.item.GunItem;
import com.craftingdead.core.item.gun.AbstractGun;
import com.craftingdead.core.item.gun.PendingHit;
import com.craftingdead.core.item.gun.simple.SimpleGunClient;
import com.craftingdead.core.living.ILiving;
import com.craftingdead.core.network.NetworkChannel;
import com.craftingdead.core.network.message.play.ValidatePendingHitMessage;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.PacketDistributor;
import org.armacraft.mod.ArmaCraft;
import org.armacraft.mod.bridge.AbstractGunBridge;
import org.armacraft.mod.network.ClientGunInfoPacket;
import org.armacraft.mod.util.GunUtils;
import org.armacraft.mod.wrapper.ClientGunDataWrapper;
import org.armacraft.mod.wrapper.ResourceLocationWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mixin(SimpleGunClient.class)
public abstract class SimpleGunClientMixin {

	@Shadow()
	@Final()
	private ClientDist client;

	@Shadow()
	@Final()
	private Minecraft minecraft;

	@Shadow()
	@Final()
	private AbstractGun<?, ?> gun;
	
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
						.joltCamera(1.0F - ((AbstractGunBridge<?, ?>) this.gun).bridge$getGunType().getAccuracyPct()
								* gun.getAttachmentMultiplier(MultiplierType.ACCURACY), true);
			}
		}

		ItemStack stack = null;
		try {
			Field gunStackField = AbstractGun.class.getDeclaredField("gunStack");
			gunStackField.setAccessible(true);
			stack = (ItemStack) gunStackField.get(this.gun);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		if(stack.getItem() instanceof GunItem) {
			GunItem gunItem = (GunItem) stack.getItem();
			ArmaCraft.networkChannel.send(PacketDistributor.SERVER.noArg(),
					new ClientGunInfoPacket(new ClientGunDataWrapper(
							ResourceLocationWrapper.of(gunItem.getRegistryName().toString()),
							gunItem.getGunType().getFireRateRPM(),
							gunItem.getGunType().getReloadDurationTicks(),
							gunItem.getGunType().getAccuracyPct(),
							gunItem.getGunType().getBulletAmountToFire())));
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
