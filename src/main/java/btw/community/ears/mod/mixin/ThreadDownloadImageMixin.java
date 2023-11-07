package btw.community.ears.mod.mixin;

import btw.AddonHandler;
import btw.community.ears.mod.EarsMod;
import btw.community.ears.mod.mojapi.DefaultSkin;
import btw.community.ears.mod.mojapi.DefaultSkinHelper;
import btw.community.ears.mod.mojapi.ProfileUtils;
import btw.community.ears.mod.mojapi.UserProfile;
import com.unascribed.ears.legacy.AWTEarsImage;
import com.unascribed.ears.main.EarsFeaturesParser;
import com.unascribed.ears.main.util.EarsStorage;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.IImageBuffer;
import net.minecraft.src.ThreadDownloadImage;
import net.minecraft.src.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Mixin(ThreadDownloadImage.class)
public class ThreadDownloadImageMixin {

    @Shadow @Final String location;
    @Shadow @Final IImageBuffer buffer;
    @Shadow @Final ThreadDownloadImageData imageData;

    @Unique private boolean hasSkin = false;

    /**
     * @author rin
     * @reason Less invasive mixins did not function in dev, and the redirect was practically an overwrite anyways
     */
    @Overwrite
    public void run() {
        {
            HttpURLConnection var1 = null;
            String urlLocation = location;
            try {
                if (this.location.startsWith("http://skins.minecraft.net/")) {
                    String userName = new File(this.location, "")
                            .getName()
                            .replaceFirst("[.][^.]+$", "");
                    UserProfile profile = ProfileUtils.getUserProfile(userName).orElse(null);
                    if (Objects.nonNull(profile)) {
                        if (Objects.nonNull(profile.getSkinUrl())) {
                            if (!profile.getSkinUrl().isEmpty()) {
                                urlLocation = profile.getSkinUrl();
                                URL var2 = new URL(urlLocation);
                                var1 = (HttpURLConnection)var2.openConnection();
                                var1.setDoInput(true);
                                var1.setDoOutput(false);
                                var1.connect();
                                if (var1.getResponseCode() / 100 == 2) {
                                    if (this.buffer == null) {
                                        this.imageData.image = ImageIO.read(var1.getInputStream());
                                    }
                                    else {
                                        this.imageData.image = this.buffer.parseUserSkin(ImageIO.read(var1.getInputStream()));
                                    }
                                    hasSkin = true;
                                } else {
                                    AddonHandler.logger.warning("Failed to download player skin for player: " + userName);
                                }
                            }
                        }
                    }
                    if (!hasSkin) {
                        // default skin impl
                        DefaultSkin skin = null;
                        if (Objects.isNull(profile)) {
                            // create 'fake' profile if no profile currently exists
                            UUID fakeUuid = UUID.randomUUID();
                            skin = DefaultSkinHelper.getDefaultSkin(fakeUuid);
                            profile = new UserProfile(fakeUuid, userName.toLowerCase(Locale.ROOT), skin.isSlim(), "", "");
                            ProfileUtils.addFakeProfile(profile);
                        }
                        ModContainer container = FabricLoader.getInstance().getModContainer("earsbtwce").orElse(null);
                        if (Objects.isNull(container)) {
                            throw new IllegalStateException("earsbtwce mod container somehow does not exist despite the mod being loaded (this should be impossible!)");
                        } else {
                           if (Objects.isNull(skin)) {
                               skin = DefaultSkinHelper.getDefaultSkin(profile.getUuid());
                           }
                           Path defaultSkinPath = container.getPath(skin.getLocation());
                            try (InputStream defaultSkinStream = Files.newInputStream(defaultSkinPath)) {
                                if (Objects.isNull(this.buffer)) {
                                    this.imageData.image = ImageIO.read(defaultSkinStream);
                                } else {
                                    this.imageData.image = this.buffer.parseUserSkin(ImageIO.read(defaultSkinStream));
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                String stack = Arrays.toString(e.getStackTrace());
                AddonHandler.logWarning(e.getMessage() + "\n" + stack);
            }
            finally
            {
                if(Objects.nonNull(var1)) {
                    var1.disconnect();
                }
            }
            if (Objects.nonNull(imageData.image)) {
                EarsMod.EARS_SKIN_FEATURES.put(this.location,
                        EarsFeaturesParser.detect(new AWTEarsImage(imageData.image),
                                EarsStorage.get(imageData.image, EarsStorage.Key.ALFALFA),
                                data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));
            }
        }
    }
}
