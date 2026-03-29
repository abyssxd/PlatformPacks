package com.abishekbhusal.platformpacks;

import org.geysermc.geyser.api.extension.ExtensionLogger;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Minimal YAML config reader — no external YAML library needed.
 *
 * Expected shape:
 * <pre>
 * packs:
 *   windows:
 *     - file.mcpack
 *   mobile:
 *     - file.mcpack
 *   ...
 * debug: false
 * </pre>
 */
public class PackConfig {

    private final Map<PlatformGroup, List<String>> packMap = new EnumMap<>(PlatformGroup.class);
    private boolean debug = false;

    private PackConfig() {}

    // -----------------------------------------------------------------------
    // Factory
    // -----------------------------------------------------------------------

    public static PackConfig load(Path configPath, ExtensionLogger logger) {
        PackConfig cfg = new PackConfig();

        if (!Files.exists(configPath)) {
            logger.warning("config.yml not found at " + configPath + " — using empty defaults.");
            return cfg;
        }

        try {
            List<String> lines = Files.readAllLines(configPath);
            cfg.parse(lines, logger);
        } catch (IOException e) {
            logger.error("Failed to read config.yml: " + e.getMessage());
        }

        return cfg;
    }

    // -----------------------------------------------------------------------
    // Minimal YAML parser
    // -----------------------------------------------------------------------

    private void parse(List<String> lines, ExtensionLogger logger) {
        // State machine: track which section / platform we are inside
        boolean inPacks = false;
        PlatformGroup currentGroup = null;

        for (String raw : lines) {
            String line = raw;

            // Strip comments
            int commentIdx = line.indexOf('#');
            if (commentIdx >= 0) {
                line = line.substring(0, commentIdx);
            }
            if (line.isBlank()) continue;

            int indent = countLeadingSpaces(line);
            String trimmed = line.trim();

            // Top-level keys (indent == 0)
            if (indent == 0) {
                inPacks = false;
                currentGroup = null;

                if (trimmed.startsWith("packs:")) {
                    inPacks = true;
                } else if (trimmed.startsWith("debug:")) {
                    String val = trimmed.substring("debug:".length()).trim();
                    debug = "true".equalsIgnoreCase(val);
                }
                continue;
            }

            if (!inPacks) continue;

            // Second-level: platform keys (indent == 2)
            if (indent == 2 && !trimmed.startsWith("-")) {
                String key = trimmed.replace(":", "").trim().toLowerCase();
                currentGroup = groupFromKey(key);
                if (currentGroup != null) {
                    packMap.putIfAbsent(currentGroup, new ArrayList<>());
                }
                continue;
            }

            // Third-level: list items (indent == 4)
            if (indent == 4 && trimmed.startsWith("-") && currentGroup != null) {
                String packName = trimmed.substring(1).trim()
                        .replace("\"", "").replace("'", "");
                if (!packName.isBlank()) {
                    packMap.get(currentGroup).add(packName);
                }
            }
        }
    }

    private static int countLeadingSpaces(String s) {
        int count = 0;
        for (char c : s.toCharArray()) {
            if (c == ' ') count++;
            else break;
        }
        return count;
    }

    private static PlatformGroup groupFromKey(String key) {
        return switch (key) {
            case "windows"     -> PlatformGroup.WINDOWS;
            case "mobile"      -> PlatformGroup.MOBILE;
            case "xbox"        -> PlatformGroup.XBOX;
            case "playstation" -> PlatformGroup.PLAYSTATION;
            case "nintendo"    -> PlatformGroup.NINTENDO;
            case "default"     -> PlatformGroup.DEFAULT;
            default            -> null;
        };
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Returns the list of pack filenames for the given platform.
     * Falls back to DEFAULT if the platform has no specific entry.
     */
    public List<String> getPacksFor(PlatformGroup group) {
        List<String> specific = packMap.get(group);
        if (specific != null && !specific.isEmpty()) {
            return Collections.unmodifiableList(specific);
        }
        List<String> def = packMap.get(PlatformGroup.DEFAULT);
        return def != null ? Collections.unmodifiableList(def) : Collections.emptyList();
    }

    public boolean isDebug() {
        return debug;
    }
}
