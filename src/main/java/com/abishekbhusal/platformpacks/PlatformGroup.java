package com.abishekbhusal.platformpacks;

/**
 * Maps Geyser's DeviceOs ordinals to friendly platform groups.
 * Bedrock DeviceOs enum order (0-indexed):
 *  0  UNKNOWN
 *  1  ANDROID
 *  2  IOS
 *  3  OSX
 *  4  AMAZON  (Fire OS)
 *  5  GEAR_VR
 *  6  HOLOLENS
 *  7  WIN10   (Windows 10 / 11)
 *  8  WIN32   (legacy)
 *  9  DEDICATED
 * 10  TV_OS   (Fire TV)
 * 11  PLAYSTATION (PS4 / PS5)
 * 12  NX      (Nintendo Switch)
 * 13  XBOX    (Xbox One / Series)
 * 14  WINDOWS_PHONE
 */
public enum PlatformGroup {

    WINDOWS,
    MOBILE,
    XBOX,
    PLAYSTATION,
    NINTENDO,
    DEFAULT;

    /**
     * Resolves a DeviceOs name (from BedrockClientData) to a PlatformGroup.
     *
     * @param deviceOsName the name() of the DeviceOs enum constant, e.g. "WIN10", "ANDROID"
     * @return the corresponding PlatformGroup
     */
    public static PlatformGroup fromDeviceOs(String deviceOsName) {
        if (deviceOsName == null) return DEFAULT;

        return switch (deviceOsName.toUpperCase()) {
            case "WIN10", "WIN32", "WINDOWS_PHONE" -> WINDOWS;

            case "ANDROID", "IOS", "GOOGLE" -> MOBILE;

            case "XBOX" -> XBOX;

            case "PLAYSTATION" -> PLAYSTATION;

            case "NX" -> NINTENDO;

            // ── Everything else (unknown) ─────
            default -> DEFAULT;
        };
    }

    /** Returns the config key used in config.yml */
    public String configKey() {
        return name().toLowerCase();
    }
}
