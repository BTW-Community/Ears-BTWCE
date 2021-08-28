package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.RawEarsImage;
import com.unascribed.ears.common.debug.EarsLog;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData extends SimpleTexture implements EarsFeaturesHolder {

	public MixinThreadDownloadImageData(ResourceLocation location) {
		super(location);
	}
	
	private EarsFeatures earsFeatures;

	@Inject(at=@At("HEAD"), method = "setImage(Lnet/minecraft/client/renderer/texture/NativeImage;)V")
	public void setImage(NativeImage cur, CallbackInfo ci) {
		EarsLog.debug("Platform:Inject", "Process player skin");
		earsFeatures = EarsFeatures.detect(new RawEarsImage(cur.makePixelArray(), cur.getWidth(), cur.getHeight(), false));
	}
	
	@Override
	public EarsFeatures getEarsFeatures() {
		return earsFeatures;
	}
	
}
