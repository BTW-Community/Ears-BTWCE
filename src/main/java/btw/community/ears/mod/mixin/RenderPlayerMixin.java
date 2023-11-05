package btw.community.ears.mod.mixin;

import btw.community.ears.mod.mojapi.ProfileUtils;
import btw.community.ears.mod.mojapi.UserProfile;
import com.unascribed.ears.legacy.LayerEars;
import com.unascribed.ears.main.TranslucentBoxModel;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.RenderLiving;
import net.minecraft.src.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin extends RenderLiving implements RenderPlayerAccessor {
    @Shadow private ModelBiped modelBipedMain;
    @Unique private ModelRenderer slimLeftArm;
    @Unique private ModelRenderer slimRightArm;

    @Unique private boolean didSlimCheck = false;
    @Unique private boolean isSlim = false;

    @Unique private RenderPlayer thisRenderer;

    @Unique private static final LayerEars layer = new LayerEars();

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Inject(method = "<init>()V", at = @At("TAIL"))
    public void earsbtwce$initInjector(CallbackInfo ci) {
        this.thisRenderer = ((RenderPlayer) (Object) this); //hopefully this works
        ModelBiped model = new ModelBiped(0, 0, 64, 64);
        modelBipedMain = model;
        this.mainModel = modelBipedMain;
        model.bipedHeadwear.cubeList.remove(0);
        TranslucentBoxModel.addBoxTo(model.bipedHeadwear, 32, 0, -4, -8, -4, 8, 8, 8, 0.5f);
        // non-flipped left arm/leg
        model.bipedLeftArm = new ModelRenderer(model, 32, 48);
        model.bipedLeftArm.addBox(-1, -2, -2, 4, 12, 4, 0);
        model.bipedLeftArm.setRotationPoint(5, 2, 0);

        model.bipedLeftLeg = new ModelRenderer(model, 16, 48);
        model.bipedLeftLeg.addBox(-2, 0, -2, 4, 12, 4, 0);
        model.bipedLeftLeg.setRotationPoint(1.9f, 12, 0);

        //slim arms
        slimLeftArm = new ModelRenderer(model, 32, 48);
        slimLeftArm.addBox(-1, -2, -2, 3, 12, 4, 0);
        slimLeftArm.setRotationPoint(5, 2.5f, 0);

        slimRightArm = new ModelRenderer(model, 40, 16);
        slimRightArm.addBox(-2, -2, -2, 3, 12, 4, 0);
        slimRightArm.setRotationPoint(-5, 2.5f, 0);
    }

    @Inject(method = "preRenderCallback(Lnet/minecraft/src/EntityLiving;F)V", at = @At("HEAD"))
    public void earsbtwce$preRenderCallbackInjector(EntityLiving par1EntityLiving, float par2, CallbackInfo ci) {
        if (!this.didSlimCheck) {
            EntityPlayer player = (EntityPlayer) par1EntityLiving;
            UserProfile profile = ProfileUtils.getUserProfile(player.username).orElse(null);
            if (Objects.nonNull(profile)) {
                this.isSlim = profile.isSlim();
            }
            this.didSlimCheck = true;
        }
        if (this.isSlim) {
            modelBipedMain.bipedRightArm = slimRightArm;
            modelBipedMain.bipedLeftArm = slimLeftArm;
        }
    }

    @Inject(method = "renderSpecials(Lnet/minecraft/src/EntityPlayer;F)V", at = @At("HEAD"))
    public void earsbtwce$renderSpecialsInjector(EntityPlayer player, float par2, CallbackInfo ci) {
        float partialTicks = Minecraft.getMinecraft().getTimer().renderPartialTicks;
        EntityPlayerSP sp = (EntityPlayerSP)player;
        layer.doRenderLayer(thisRenderer, sp,
                sp.prevLimbYaw + (player.limbYaw - player.prevLimbYaw) * partialTicks,
                            partialTicks);
    }

    @Inject(method = "renderFirstPersonArm(Lnet/minecraft/src/EntityPlayer;)V", at = @At("HEAD"))
    public void earsbtwce$renderFirstPersonArmInjector(EntityPlayer par1EntityPlayer, CallbackInfo ci) {
        layer.renderRightArm(thisRenderer, par1EntityPlayer);
    }
}
