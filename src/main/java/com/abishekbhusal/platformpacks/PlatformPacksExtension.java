package com.abishekbhusal.platformpacks;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.bedrock.SessionLoadResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;
import org.geysermc.geyser.session.GeyserSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlatformPacksExtension implements Extension {

    private PackConfig packConfig;
    private final Map<String, ResourcePack> packCache = new ConcurrentHashMap<>();

    @Subscribe
    public void onPreInitialize(GeyserPreInitializeEvent event) {
        saveDefaultConfig();
        packConfig = PackConfig.load(dataFolder().resolve("config.yml"), logger());
        logger().info("PlatformPacks loaded. Platform-specific resource packs are active.");
    }

    @Subscribe
    public void onSessionLoadResourcePacks(SessionLoadResourcePacksEvent event) {
        if (packConfig == null) return;

        String deviceOsName = "UNKNOWN";
        if (event.connection() instanceof GeyserSession session) {
            try {
                deviceOsName = session.getClientData().getDeviceOs().name();
            } catch (Exception e) {
                logger().warning("[PlatformPacks] Could not read DeviceOs: " + e.getMessage());
            }
        }

        PlatformGroup group = PlatformGroup.fromDeviceOs(deviceOsName);

        if (packConfig.isDebug()) {
            logger().info("[PlatformPacks] " + event.connection().bedrockUsername()
                    + " connecting | DeviceOs=" + deviceOsName + " → " + group.name());
        }

        List<String> packFiles = packConfig.getPacksFor(group);
        if (packFiles.isEmpty()) {
            if (packConfig.isDebug()) {
                logger().info("[PlatformPacks] No packs configured for " + group.name());
            }
            return;
        }

        Path packsDir = dataFolder().resolve("packs");

        for (String packFileName : packFiles) {
            Path packPath = packsDir.resolve(packFileName);

            if (!Files.exists(packPath)) {
                logger().warning("[PlatformPacks] Pack file not found: " + packPath
                        + "  (platform: " + group.name() + ")");
                continue;
            }

            try {
                ResourcePack pack = packCache.computeIfAbsent(
                        packPath.toAbsolutePath().toString(),
                        key -> ResourcePack.create(PackCodec.path(packPath))
                );
                event.register(pack);

                if (packConfig.isDebug()) {
                    logger().info("[PlatformPacks] Registered '" + packFileName
                            + "' for " + group.name());
                }
            } catch (Exception e) {
                logger().error("[PlatformPacks] Error loading '"
                        + packFileName + "': " + e.getMessage());
            }
        }
    }

    private void saveDefaultConfig() {
        Path configPath = dataFolder().resolve("config.yml");
        if (Files.exists(configPath)) return;
        try {
            Files.createDirectories(dataFolder());
            InputStream in = getClass().getResourceAsStream("/config.yml");
            if (in != null) Files.copy(in, configPath);
        } catch (IOException e) {
            logger().error("Could not save default config.yml: " + e.getMessage());
        }
    }
}