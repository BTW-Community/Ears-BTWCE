package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import btw.community.ears.mod.EarsMod;
import btw.community.ears.mod.mojapi.ProfileUtils;
import btw.community.ears.mod.mojapi.UserProfile;
import com.unascribed.ears.legacy.AWTEarsImage;
import com.unascribed.ears.main.EarsFeaturesParser;
import com.unascribed.ears.main.util.EarsStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ThreadDownloadImage extends Thread
{
    final static Map<String, String> uuids = new HashMap<String, String>();
    /** The URL of the image to download. */
    final String location;

    /** The image buffer to use. */
    final IImageBuffer buffer;

    /** The image data. */
    final ThreadDownloadImageData imageData;

    ThreadDownloadImage(ThreadDownloadImageData par1, String par2Str, IImageBuffer par3IImageBuffer)
    {
        this.imageData = par1;
        this.location = par2Str;
        this.buffer = par3IImageBuffer;
    }

    public void run() {
        HttpURLConnection var1 = null;
        String urlLocation = this.location;

        try {
            if (this.location.startsWith("http://skins.minecraft.net/")) {
                String userName = new File(this.location, "")
                        .getName()
                        .replaceFirst("[.][^.]+$", "");

                UserProfile profile = ProfileUtils.getUserProfile(userName).orElse(null);
                if (Objects.nonNull(profile)) {
                    if (Objects.nonNull(profile.getSkinUrl()) && !profile.getSkinUrl().isEmpty()) {
                        urlLocation = profile.getSkinUrl();
                    }
                }
            }

            URL var2 = new URL(urlLocation);
            var1 = (HttpURLConnection)var2.openConnection();
            var1.setDoInput(true);
            var1.setDoOutput(false);
            var1.connect();

            if (var1.getResponseCode() / 100 == 4)
            {
                return;
            }

            if (this.buffer == null)
            {
                this.imageData.image = ImageIO.read(var1.getInputStream());
            }
            else
            {
                this.imageData.image = this.buffer.parseUserSkin(ImageIO.read(var1.getInputStream()));
            }
        }
        catch (Exception var6)
        {
            var6.printStackTrace();
        }
        finally
        {
            if(Objects.nonNull(var1)) {
                var1.disconnect();
            }
        }
        if (Objects.nonNull(imageData.image)) {
            EarsMod.earsSkinFeatures.put(this.location,
                    EarsFeaturesParser.detect(new AWTEarsImage(imageData.image),
                            EarsStorage.get(imageData.image, EarsStorage.Key.ALFALFA),
                            data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));
        }
    }
}