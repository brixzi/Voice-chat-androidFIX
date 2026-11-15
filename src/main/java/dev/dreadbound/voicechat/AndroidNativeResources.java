package dev.dreadbound.voicechat;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Fail-open and client-only: never changes Java Sound capture or the original SVC JAR. */
public final class AndroidNativeResources {
    private static final String ANDROID_PREFIX = "/natives/android-aarch64/";
    private static final Set<String> SUPPORTED = Set.of(
            "librnnoise4j.so", "libopus4j.so", "libspeex4j.so", "liblame4j.so"
    );
    private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();

    private AndroidNativeResources() {}

    public static boolean isAndroidArm64() {
        String arch = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
        if (!(arch.equals("aarch64") || arch.equals("arm64"))) return false;
        String androidRoot = System.getenv("ANDROID_ROOT");
        return (androidRoot != null && !androidRoot.isBlank())
                || Files.isDirectory(Path.of("/apex"))
                || Files.isRegularFile(Path.of("/system/build.prop"))
                || "dalvik".equalsIgnoreCase(System.getProperty("java.vm.name", ""));
    }

    /** Returns null to preserve the original Simple Voice Chat behavior unchanged. */
    public static InputStream lookup(String originalPath) {
        if (!isAndroidArm64() || originalPath == null) return null;
        String path = originalPath.replace('\\', '/');
        if (!path.contains("linux-aarch64")) return null;
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (!SUPPORTED.contains(name)) return null;
        InputStream stream = AndroidNativeResources.class.getResourceAsStream(ANDROID_PREFIX + name);
        if (stream == null) {
            if (LOGGED.add("MISSING:" + name)) {
                DreadboundVoicechatAndroid.LOGGER.error("Android replacement missing: {}", name);
            }
            return null;
        }
        if (LOGGED.add("OK:" + name)) {
            DreadboundVoicechatAndroid.LOGGER.info("Redirecting Simple Voice Chat native {} to Android AArch64 build", name);
        }
        return stream;
    }
}
