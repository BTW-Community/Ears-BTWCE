package btw.community.ears.btwaddon;

import btw.AddonHandler;
import btw.BTWAddon;

public class EarsBTWAddon extends BTWAddon {
    private static EarsBTWAddon instance;

    private EarsBTWAddon() {
        super("Example Name", "0.1.0", "Ex");
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

    public static EarsBTWAddon getInstance() {
        if (instance == null)
            instance = new EarsBTWAddon();
        return instance;
    }
}
