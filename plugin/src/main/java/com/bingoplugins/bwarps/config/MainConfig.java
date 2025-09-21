package com.bingoplugins.bwarps.config;

import com.bingoplugins.bwarps.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.bingoplugins.bwarps.Plugin.LOGGER;

public class MainConfig extends Config {
    public MainConfig() {
        super("config");

        addDefaults(Map.of(
                "warps", Map.of()
        ));

        saveConfig();
    }

    @Override
    protected boolean isConfigValid() {
        ConfigurationSection warpsSec = getConfigurationSection("warps");

        if (warpsSec == null) return true;

        final AtomicBoolean verdict = new AtomicBoolean(true);

        warpsSec.getKeys(false).forEach(warpID -> {
            final ConfigurationSection sec = warpsSec.getConfigurationSection(warpID);

            if (sec == null) {
                LOGGER.severe("Warp '%s' is not a section.".formatted(warpID));
                verdict.set(false);
                return;
            }

            final String worldName = sec.getString("world");

            if (worldName == null || Bukkit.getWorld(worldName) == null) {
                LOGGER.severe("Warp '%s' has invalid or missing world.".formatted(warpID));
                verdict.set(false);
            }

            if (!sec.isDouble("x") || !sec.isDouble("y") || !sec.isDouble("z")) {
                LOGGER.severe("Warp '%s' has missing or invalid coordinates.".formatted(warpID));
                verdict.set(false);
            }

            if (!sec.isSet("yaw")) sec.set("yaw", 0.0);
            if (!sec.isSet("pitch")) sec.set("pitch", 0.0);

            if (sec.getString("name") == null) {
                LOGGER.warning("Warp '%s' has no name, using ID as fallback.".formatted(warpID));
                sec.set("name", warpID);
            }
        });

        return verdict.get();
    }
}