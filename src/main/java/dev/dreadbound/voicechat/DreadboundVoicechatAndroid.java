package dev.dreadbound.voicechat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * Only a client-side native library redirector. No microphone capture changes.
 * The actual Simple Voice Chat mod remains installed, unmodified.
 */
@Mod(DreadboundVoicechatAndroid.MOD_ID)
public final class DreadboundVoicechatAndroid {
    public static final String MOD_ID = "dreadbound_voicechat_android";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DreadboundVoicechatAndroid() {
        if (AndroidNativeResources.isAndroidArm64()) {
            LOGGER.info("Dreadbound Android VoiceChat native library redirect enabled (experimental)");
        } else {
            LOGGER.warn("Dreadbound Android VoiceChat fix inactive: runtime is not confirmed Android ARM64");
        }
    }
}
