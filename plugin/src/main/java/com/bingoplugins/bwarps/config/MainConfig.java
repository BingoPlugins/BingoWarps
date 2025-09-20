package com.bingoplugins.bwarps.config;

import com.bingoplugins.bwarps.utils.Config;
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
        final ConfigurationSection warpSection = getConfigurationSection("warps");

        AtomicBoolean verdict = new AtomicBoolean(true);

        warpSection.getKeys(false).forEach(warpID -> {
            if (warpSection.get("%s.location".formatted(warpID)) == null) {
                LOGGER.severe("Warp with ID: %s, has a missing location.".formatted(warpID));
                verdict.set(false);
                return;
            }

            if (warpSection.getLocation("%s.location".formatted(warpID)) == null) {
                LOGGER.severe("Warp with ID: %s, has an invalid location.".formatted(warpID));
                verdict.set(false);
            }
        });

        return verdict.get();
    }
}