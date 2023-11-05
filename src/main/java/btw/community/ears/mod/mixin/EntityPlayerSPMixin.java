package btw.community.ears.mod.mixin;

import btw.community.ears.mod.mojapi.ProfileUtils;
import btw.community.ears.mod.mojapi.UserProfile;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin extends EntityPlayer {

    public EntityPlayerSPMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    public void earsbtwce$entityPlayerSPInitInjector(CallbackInfo ci) {
        UserProfile profile = ProfileUtils.getUserProfile(this.username).orElse(null);
        if (Objects.isNull(profile)) {
            //fallback behavior: create a fake skinless profile
            UUID fallbackGen = UUID.randomUUID();
            UserProfile fakeProfile = new UserProfile(fallbackGen, this.username, false, "", "");
            ProfileUtils.addFakeProfile(fakeProfile);
        }
    }
}
