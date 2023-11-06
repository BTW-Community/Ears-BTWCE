package btw.community.ears.mod.mojapi;

public final class DefaultSkin {
    private final String location;
    private final boolean isSlim;

    public DefaultSkin(String location, boolean isSlim) {
        this.location = location;
        this.isSlim = isSlim;
    }
    public String getLocation() {
        return location;
    }

    public boolean isSlim() {
        return isSlim;
    }
}
