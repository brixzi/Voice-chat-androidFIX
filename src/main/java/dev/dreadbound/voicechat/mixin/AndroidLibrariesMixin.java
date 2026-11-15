package dev.dreadbound.voicechat.mixin;

import dev.dreadbound.voicechat.AndroidNativeResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

/**
 * The four JNI wrappers are bundled with SVC 2.6.x. They each expose the same
 * static LibraryLoader.getResource(String) signature used by the Fabric fix.
 * remap=false because these targets are not obfuscated Minecraft classes.
 */
@Mixin(targets = {
        "de.maxhenkel.rnnoise4j.LibraryLoader",
        "de.maxhenkel.opus4j.LibraryLoader",
        "de.maxhenkel.speex4j.LibraryLoader",
        "de.maxhenkel.lame4j.LibraryLoader"
}, remap = false)
public abstract class AndroidLibrariesMixin {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dreadbound$replaceLinuxNative(String path, CallbackInfoReturnable<InputStream> cir) {
        InputStream androidNative = AndroidNativeResources.lookup(path);
        if (androidNative != null) cir.setReturnValue(androidNative);
    }
}
