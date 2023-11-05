package btw.community.ears.mod.mojapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class UserProfile {
    private final UUID uuid;
    private final String username;
    private final boolean isSlim;
    private final String skinUrl;
    private final String capeUrl;
    private final boolean errored;

    public UserProfile(UUID uuid, String username, boolean isSlim, @Nullable String skinUrl, @Nullable String capeUrl) {
        this.uuid = uuid;
        this.username = username;
        this.isSlim = isSlim;
        this.skinUrl = skinUrl;
        this.capeUrl = capeUrl;
        this.errored = false;
    }

    /**
     * Errored profile. Implemented because (Concurrent)HashMaps cannot have null values.
     */
    public UserProfile() {
        this.uuid = UUID.randomUUID();
        this.username = "";
        this.isSlim = false;
        this.skinUrl = "";
        this.capeUrl = "";
        this.errored = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isSlim() {
        return isSlim;
    }

    public @Nullable String getSkinUrl() {
        return skinUrl;
    }

    public @Nullable String getCapeUrl() {
        return capeUrl;
    }

    public String getUsername() {
        return username;
    }

    public boolean isErrored() {
        return errored;
    }

    public static class UserProfileDeserializer implements JsonDeserializer<UserProfile> {
        @Override
        public UserProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            UUID uuid;
            String username = "";
            boolean isSlim = false;
            String skinUrl = null;
            String capeUrl = null;
            if (json.isJsonObject()) {
                JsonObject profileObj = json.getAsJsonObject();
                if (profileObj.has("profileId")) {
                    String mojankUuidStr = profileObj.get("profileId").getAsString();
                    uuid = UUID.fromString(ProfileUtils.fixUuidString(mojankUuidStr));
                    username = profileObj.get("profileName").getAsString();
                    JsonElement textures = profileObj.get("textures");
                    if (textures.isJsonObject()) {
                        JsonObject texturesObj = textures.getAsJsonObject();
                        if (texturesObj.has("SKIN")) {
                            JsonElement skin = texturesObj.get("SKIN");
                            if (skin.isJsonObject()) {
                                JsonObject skinObj = skin.getAsJsonObject();
                                skinUrl = skinObj.get("url").getAsString();
                                if (skinObj.has("metadata")) {
                                    JsonElement metadata = skinObj.get("metadata");
                                    if (metadata.isJsonObject()) {
                                        JsonObject metadataObj = metadata.getAsJsonObject();
                                        if (metadataObj.has("model")) {
                                            JsonElement model = metadataObj.get("model");
                                            //mojang has a tendency to change how this works often for some forsaken reason, so we're going to try two ways here:
                                            if (model.isJsonPrimitive()) {
                                                String slimCheck0 = model.getAsString();
                                                if (slimCheck0.equals("slim")) {
                                                    isSlim = true;
                                                } else {
                                                    isSlim = model.getAsBoolean();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (texturesObj.has("CAPE")) {
                            JsonElement cape = texturesObj.get("CAPE");
                            if (cape.isJsonObject()) {
                                JsonObject capeObj = cape.getAsJsonObject();
                                capeUrl = capeObj.get("url").getAsString();
                            }
                        }
                    }
                } else {
                    //should only happen in dev envs, maybe?
                    uuid = UUID.randomUUID();
                }
            } else {
                throw new JsonParseException("Root decoded properties json is not an object, must be malformed!");
            }
            return new UserProfile(uuid, username, isSlim, skinUrl, capeUrl);
        }
    }
}
