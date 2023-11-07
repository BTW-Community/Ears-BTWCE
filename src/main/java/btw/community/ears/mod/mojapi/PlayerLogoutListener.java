package btw.community.ears.mod.mojapi;

public interface PlayerLogoutListener {
    void onPlayerLogout(String caselessUsername);

    default void registerListener(){
        ProfileUtils.LISTENERS.add(this);
    }
}
