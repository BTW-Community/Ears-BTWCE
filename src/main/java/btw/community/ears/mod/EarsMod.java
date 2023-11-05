package btw.community.ears.mod;

import btw.community.ears.mod.mojapi.UserProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unascribed.ears.api.features.EarsFeatures;
import net.fabricmc.api.ClientModInitializer;

import java.util.Map;
import java.util.WeakHashMap;

public class EarsMod implements ClientModInitializer {

	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(UserProfile.class, new UserProfile.UserProfileDeserializer())
			.create();
	@Override
	public void onInitializeClient() {

	}
}
