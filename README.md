# Dreadbound VoiceChat Android Fix: Forge 1.20.1 (EXPERIMENTAL SOURCE)

**THIS IS NOT A TESTED, INSTALLABLE MOD JAR.** This repository contains a prototype for adapting the loading strategy of the Fabric project `melon4ik666/voicechat-android-fix` to Forge. It has not been compiled or run in Zalith, and may need further fixes to the mixin/loader to work correctly.

Target: Android AArch64 (Zalith Launcher 2), Forge 1.20.1 / 47.4.29, Simple Voice Chat 2.6.27. Client only. No changes to mic capture, no server installation.

## Design

1. Declare a Forge mod and register the Mixin configuration through the JAR manifest.
2. Intercept `getResource(String)` for four `de.maxhenkel.*4j.LibraryLoader` classes, preserving the signature used by the upstream Fabric solution.
3. On Android AArch64 only, serve Android NDK-compiled `.so` resources from this JAR.
4. Leave original SVC resources and microphone capture untouched. Log redirected libraries. If missing, fall back to original and allow its normal error reporting.

## Build with GitHub Actions

Upload these files as a GitHub repository. The `Build experimental Forge port` workflow downloads four pinned Android ARM64 binaries from the third-party upstream repository, verifies their Git blob SHA-1 and ELF architecture, and runs ForgeGradle. The resulting experimental JAR, if build succeeds, appears as a workflow artifact. **Do not install or distribute as validated until it passes device testing and licensing review.**

## What must be verified on the Galaxy A36

- JAR launches Forge 47.4.29 with SVC 2.6.27 without crashing.
- All four libraries show redirect logs and initialize successfully (Opus, RNNoise, Speex, LAME).
- `V > Settings > Denoiser` becomes clickable and denoises a live recording.
- Mic input still works without a headset; verify AGC and outgoing/incoming voice.
- After that, validate e4mc voice bridge and Revervox voice mimicry.

## Attribution and security

Inspired by `https://github.com/melon4ik666/voicechat-android-fix` (MIT-labeled code, third-party binary resources). Java logic here is independently adapted to Forge. The workflow obtains precompiled native code from upstream; **the binaries have not been audited**. Check the relevant JNI ABI and licenses of `lame4j`, `opus4j`, `rnnoise4j`, and `speex4j` before distributing a resulting JAR. Original Simple Voice Chat remains a separate mod.
