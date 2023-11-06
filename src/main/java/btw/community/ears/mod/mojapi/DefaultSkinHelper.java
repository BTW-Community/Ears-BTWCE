package btw.community.ears.mod.mojapi;

import java.util.UUID;

public final class DefaultSkinHelper {
    private static final DefaultSkin[] DEFAULT_SKINS = new DefaultSkin[] {
            new DefaultSkin("assets/earsbtwce/textures/alex_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/alex_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/ari_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/ari_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/efe_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/efe_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/kai_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/kai_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/makena_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/makena_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/noor_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/noor_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/steve_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/steve_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/sunny_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/sunny_wide.png", false),
            new DefaultSkin("assets/earsbtwce/textures/zuri_slim.png", true),
            new DefaultSkin("assets/earsbtwce/textures/zuri_wide.png", false)};

    public static DefaultSkin getDefaultSkin(UUID uuid) {
        return DEFAULT_SKINS[Math.floorMod(uuid.hashCode(), DEFAULT_SKINS.length)];
    }

}
