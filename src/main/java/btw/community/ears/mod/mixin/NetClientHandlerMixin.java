package btw.community.ears.mod.mixin;

import btw.community.ears.mod.mojapi.ProfileUtils;
import net.minecraft.src.GuiPlayerInfo;
import net.minecraft.src.NetClientHandler;
import net.minecraft.src.Packet201PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Inject(method = "handlePlayerInfo", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void earsbtwce$handlePlayerInfoInjector(Packet201PlayerInfo par1Packet201PlayerInfo, CallbackInfo ci, GuiPlayerInfo var2){
        if (var2 != null && !par1Packet201PlayerInfo.isConnected) {
            String name = var2.name;
            ProfileUtils.removeUserProfile(name);
        }
    }
}
