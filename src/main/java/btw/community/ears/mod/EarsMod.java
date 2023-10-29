package btw.community.ears.mod;

import btw.AddonHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class EarsMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		AddonHandler.logMessage("Hello Fabric world!");
	}
}
