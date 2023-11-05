package btw.community.ears.mod.mixin;

import com.unascribed.ears.legacy.AWTEarsImage;
import com.unascribed.ears.main.EarsCommon;
import com.unascribed.ears.main.util.EarsStorage;
import net.minecraft.src.IImageBuffer;
import net.minecraft.src.ImageBufferDownload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * Redirect cloned from <a href="https://github.com/unascribed/Ears/blob/trunk/platform-forge-1.5/src/main/java/com/unascribed/ears/Ears.java#L119">1.5.x ears fork</a>
 */
@Mixin(ImageBufferDownload.class)
public abstract class ImageBufferDownloadMixin implements IImageBuffer {

    @Shadow
    private int[] imageData;

    @Shadow
    private int imageWidth;

    @Shadow
    private int imageHeight;

    @Shadow
    protected abstract void setAreaOpaque(int par1, int par2, int par3, int par4);

    @Shadow
    protected abstract void setAreaTransparent(int par1, int par2, int par3, int par4);

    @Inject(method = "parseUserSkin(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;",
                at = @At("HEAD"),
            cancellable = true)
    public void earsBTWCE$InjectParseUserSkin(BufferedImage image, CallbackInfoReturnable<BufferedImage> cir) {
        if (image == null) {
            cir.setReturnValue(null);
        }
        else {
            imageWidth = 64;
            imageHeight = 64;
            BufferedImage newImg = new BufferedImage(64, 64, 2);
            Graphics g = newImg.getGraphics();
            g.drawImage(image, 0, 0, null);
            if(image.getHeight() == 32) {
                g.drawImage(newImg, 24, 48, 20, 52, 4, 16, 8, 20, null);
                g.drawImage(newImg, 28, 48, 24, 52, 8, 16, 12, 20, null);
                g.drawImage(newImg, 20, 52, 16, 64, 8, 20, 12, 32, null);
                g.drawImage(newImg, 24, 52, 20, 64, 4, 20, 8, 32, null);
                g.drawImage(newImg, 28, 52, 24, 64, 0, 20, 4, 32, null);
                g.drawImage(newImg, 32, 52, 28, 64, 12, 20, 16, 32, null);
                g.drawImage(newImg, 40, 48, 36, 52, 44, 16, 48, 20, null);
                g.drawImage(newImg, 44, 48, 40, 52, 48, 16, 52, 20, null);
                g.drawImage(newImg, 36, 52, 32, 64, 48, 20, 52, 32, null);
                g.drawImage(newImg, 40, 52, 36, 64, 44, 20, 48, 32, null);
                g.drawImage(newImg, 44, 52, 40, 64, 40, 20, 44, 32, null);
                g.drawImage(newImg, 48, 52, 44, 64, 52, 20, 56, 32, null);
            }
            g.dispose();
            EarsStorage.put(newImg, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new AWTEarsImage(newImg)));
            imageData = ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData();
            EarsCommon.carefullyStripAlpha(this::setAreaOpaque, true);
            setAreaTransparent(32, 0, 64, 32);
            setAreaTransparent(0, 32, 16, 48);
            setAreaTransparent(16, 32, 40, 48);
            setAreaTransparent(40, 32, 56, 48);
            setAreaTransparent(0, 48, 16, 64);
            setAreaTransparent(48, 48, 64, 64);
            cir.setReturnValue(newImg);
        }

    }

}
