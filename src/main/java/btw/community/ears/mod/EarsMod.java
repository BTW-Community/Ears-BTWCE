package btw.community.ears.mod;

import btw.AddonHandler;
import btw.community.ears.mod.mojapi.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unascribed.ears.api.features.EarsFeatures;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import java.util.Map;
import java.util.WeakHashMap;

public class EarsMod implements ClientModInitializer {

	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(UserProfile.class, new UserProfile.UserProfileDeserializer())
			.create();
	@Override
	public void onInitializeClient() {
		AddonHandler.logMessage("Hello Fabric world!");
	}
}
