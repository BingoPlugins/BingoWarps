package com.bingoplugins.bwarps.warps;

import com.bingoplugins.bwarps.api.warps.Warp;
import com.bingoplugins.bwarps.config.MainConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.bingoplugins.bwarps.Plugin.PLUGIN;

public class WarpManager {
    private final ConcurrentHashMap<String, Warp> warps = new ConcurrentHashMap<>();

    public Optional<Warp> getWarp(@NotNull String warpID) {
        return Optional.ofNullable(warps.get(warpID));
    }

    public boolean createWarp(@NotNull String warpID, @NotNull Warp warp) {
        return warps.putIfAbsent(warpID, warp) == null;
    }

    public boolean deleteWarp(@NotNull String warpID) {
        return warps.remove(warpID) != null;
    }

    public @NotNull Set<String> getWarpIDs() {
        return warps.keySet();
    }

    public void load() {
        final MainConfig config = PLUGIN.getMainConfig();
        final ConfigurationSection section = config.getConfigurationSection("warps");

        section.getKeys(false).forEach(warpID -> {
            final ConfigurationSection warpSection = section.getConfigurationSection(warpID);

            createWarp(warpID, new Warp(warpSection.getLocation("location")));

            if (warpSection.get("description") != null) {
                getWarp(warpID).ifPresent(warp -> warp.setDescription(warpSection.getString("description")));
            }
        });
    }

    public void save() {
        final MainConfig mainConfig = PLUGIN.getMainConfig();

        warps.forEach((warpID, warp) -> {
            mainConfig.set("warps.%s.location".formatted(warpID), warp.getLocation());

            if (warp.getDescription().isEmpty()) {
                mainConfig.set("warps.%s.description".formatted(warpID), warp.getDescription());
            }
        });
    }
}
