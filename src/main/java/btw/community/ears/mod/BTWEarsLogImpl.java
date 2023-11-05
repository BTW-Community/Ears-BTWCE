package btw.community.ears.mod;

import java.util.HashSet;
import java.util.Set;

public class BTWEarsLogImpl {
    private static final boolean DEBUG_TO_STDOUT = false;

    public static boolean checkDebug() {
        return Boolean.getBoolean("ears.debug");
    }

    public static Set<String> checkOnlyDebug() {
        String s = System.getProperty("ears.debug.only");
        if (s != null) {
            Set<String> set = new HashSet<String>();
            for (String en : s.split(",")) {
                set.add(en);
            }
            return set;
        }
        return null;
    }

    public static String buildMsg(int secs, int millis, String tag, String msg) {
        return String.format("[T+%03d.%03d] (%s): %s", secs, millis, tag, msg);
    }

    public static void log(String msg) {
        if (DEBUG_TO_STDOUT) {
            System.out.println("[Ears] "+msg);
        }
    }
}
