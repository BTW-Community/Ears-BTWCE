package btw.community.ears.mod.mojapi;

import btw.AddonHandler;
import btw.BTWMod;
import btw.community.ears.mod.EarsMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Environment(EnvType.CLIENT)
public final class ProfileUtils {

    //TODO: SET TO FALSE IN PROD!!!
    private static final boolean TESTING = false;
    private static final String testUsername = "_rin01";
    static final ConcurrentHashMap<String, UserProfile> userProfileCache = new ConcurrentHashMap<>();

    public static Optional<UserProfile> getUserProfile(String username) {
        String usernameActual = TESTING ? testUsername : username;
        UserProfile possible;
        if (userProfileCache.containsKey(usernameActual)) {
            possible = userProfileCache.get(usernameActual);
        } else {
            getProfileInner(usernameActual);
            possible = userProfileCache.getOrDefault(usernameActual, new UserProfile());
        }
        if (possible.isErrored()) {
            possible = null;
        }
        return Optional.ofNullable(possible);
    }
    public static boolean removeUserProfile(String username) {
        UserProfile temp = userProfileCache.remove(username);
        return Objects.nonNull(temp);
    }

    /**
     * Internal use only. Used to add fake user profiles when none are found on skin lookup.
     */
    @ApiStatus.Internal
    public static void addFakeProfile(UserProfile profile) {
        userProfileCache.putIfAbsent(profile.getUsername(), profile);
    }

    private static void getProfileInner(String username) {
        HttpURLConnection uuidConnection = null;
        HttpURLConnection profileConnection = null;
        UserProfile profile = new UserProfile(); //'errored' profile. should always be overwritten unless something is rather broken
        try {
            String fetchUuidAddress = "https://api.mojang.com/users/profiles/minecraft/" + username;
            String uuidStr = null;
            URL uuidURL = new URL(fetchUuidAddress);
            uuidConnection = (HttpURLConnection) uuidURL.openConnection();
            uuidConnection.setDoInput(true);
            uuidConnection.setDoOutput(false);
            uuidConnection.connect();
            if (uuidConnection.getResponseCode() / 100 == 2) {
                JsonParser parser = new JsonParser();
                JsonElement uuidJson = parser.parse(new InputStreamReader(uuidConnection.getInputStream()));
                if (uuidJson.isJsonObject()) {
                    //should always be true unless json is malformed
                    JsonObject uuidObj = uuidJson.getAsJsonObject();
                    String sanityUser = uuidObj.get("name").getAsString().toLowerCase(Locale.ROOT);
                    if (!sanityUser.equals(username.toLowerCase(Locale.ROOT))) {
                        throw new IOException("Username mismatch between actual and username in uuid fetch response.\n Actual: " + username + "\nFetched: " + sanityUser);
                    }
                    uuidStr = uuidObj.get("id").getAsString();
                } else {
                    throw new JsonParseException("Presumably malformed json received while fetching uuid.");
                }
            }

            if (Objects.nonNull(uuidStr)) {
                String fetchProfileAddress = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr;
                URL profileURL = new URL(fetchProfileAddress);
                profileConnection = (HttpURLConnection) profileURL.openConnection();
                profileConnection.setDoInput(true);
                profileConnection.setDoOutput(false);
                profileConnection.connect();
                if (profileConnection.getResponseCode() / 100 == 2) {
                    JsonParser parser = new JsonParser();
                    JsonElement rootJson = parser.parse(new InputStreamReader(profileConnection.getInputStream()));
                    if (rootJson.isJsonObject()) {
                        JsonObject rootObj = rootJson.getAsJsonObject();
                        String sanityUuid = rootObj.get("id").getAsString();
                        String sanityUser = rootObj.get("name").getAsString().toLowerCase(Locale.ROOT);
                        if (sanityUser.equals(username) && sanityUuid.equals(uuidStr)) {
                            JsonElement properties = rootObj.get("properties");
                            if (properties.isJsonArray()) {
                                JsonArray propertiesArray = properties.getAsJsonArray(); //should only ever contain one entry
                                JsonElement propertiesInternal = propertiesArray.get(0);
                                if (propertiesInternal.isJsonObject()) {
                                    JsonObject propertiesObj = propertiesInternal.getAsJsonObject();
                                    JsonElement propertyName = propertiesObj.get("name");
                                    if (propertyName.getAsString().equals("textures")) {
                                        JsonElement valueElement = propertiesObj.get("value");
                                        String value = valueElement.getAsString(); //base64 encoded
                                        byte[] decoded = Base64.getDecoder().decode(value);
                                        String valueDecoded = new String(decoded, StandardCharsets.UTF_8);
                                        profile = EarsMod.GSON.fromJson(valueDecoded, UserProfile.class);
                                    } else {
                                        throw new JsonParseException("Unexpected value in position 0 of properties json array (presumably malformed): " + propertyName.getAsString());
                                    }
                                } else {
                                    throw new JsonParseException("Unexpected json element type in position 0 of properties json array (presumably malformed)");
                                }
                            } else {
                                throw new JsonParseException("Presumably malformed json received while fetching user profile details.");
                            }
                        } else {
                            if (!sanityUser.toLowerCase(Locale.ROOT).equals(username.toLowerCase(Locale.ROOT))) {
                                throw new IOException("Username mismatch between actual and user profile header.\nActual: " + username + "\nHeader: " + sanityUser);
                            } else {
                                throw new IOException("Uuid mismatch between previously fetched and user profile header.\nPrevious: " + uuidStr + "\nHeader: " + sanityUuid);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            AddonHandler.logMessage(e.getMessage());
        } finally {
            if (uuidConnection != null) {
                uuidConnection.disconnect();
            }
            if (profileConnection != null) {
                profileConnection.disconnect();
            }
            ProfileUtils.userProfileCache.put(username, profile);
        }
    }

    public static String fixUuidString(String shortUuidString) {
        StringBuilder idBuff = new StringBuilder(shortUuidString);
        idBuff.insert(20, '-');
        idBuff.insert(16, '-');
        idBuff.insert(12, '-');
        idBuff.insert(8, '-');
        return idBuff.toString();
    }
}
