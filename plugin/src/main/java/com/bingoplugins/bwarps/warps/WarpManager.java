package com.bingoplugins.bwarps.warps;

import com.bingoplugins.bwarps.api.returncodes.TeleportReturnCode;
import com.bingoplugins.bwarps.api.returncodes.WarpOperationsReturnCode;
import com.bingoplugins.bwarps.api.warps.Warp;
import com.bingoplugins.bwarps.config.MainConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static com.bingoplugins.bwarps.Plugin.LOGGER;
import static com.bingoplugins.bwarps.Plugin.PLUGIN;

public class WarpManager {
    private final ConcurrentHashMap<String, Warp> warps = new ConcurrentHashMap<>();

    public @NotNull Optional<Warp> getWarp(@NotNull String warpID) {
        return Optional.ofNullable(warps.get(warpID));
    }

    public @NotNull WarpOperationsReturnCode createWarp(@NotNull String warpID, @NotNull Warp warp) {
        return warps.putIfAbsent(warpID, warp) == null ? WarpOperationsReturnCode.SUCCESS : WarpOperationsReturnCode.WARP_ALREADY_EXISTS;
    }

    public @NotNull WarpOperationsReturnCode deleteWarp(@NotNull String warpID) {
        return warps.remove(warpID) == null ? WarpOperationsReturnCode.WARP_DOESNT_EXIST : WarpOperationsReturnCode.SUCCESS;
    }

    public @NotNull Set<String> getWarpIDs() {
        return warps.keySet();
    }

    public void load() {
        final MainConfig config = PLUGIN.getMainConfig();
        final ConfigurationSection section = config.getConfigurationSection("warps");

        if (section == null) return;

        int loaded = 0;
        int skipped = 0;

        for (final String warpID : section.getKeys(false)) {
            final ConfigurationSection warpSec = section.getConfigurationSection(warpID);

            if (warpSec == null) {
                LOGGER.severe("Warp '%s' is not a section. Skipping.".formatted(warpID));
                skipped++;
                continue;
            }

            final String worldName = warpSec.getString("world");

            if (worldName == null || PLUGIN.getServer().getWorld(worldName) == null) {
                LOGGER.severe("Warp '%s' has invalid world. Skipping.".formatted(warpID));
                skipped++;
                continue;
            }

            if (!warpSec.isDouble("x") || !warpSec.isDouble("y") || !warpSec.isDouble("z")) {
                LOGGER.severe("Warp '%s' has invalid coordinates. Skipping.".formatted(warpID));
                skipped++;
                continue;
            }

            final String name = warpSec.getString("name", warpID);
            final double x = warpSec.getDouble("x");
            final double y = warpSec.getDouble("y");
            final double z = warpSec.getDouble("z");
            final float yaw = (float) warpSec.getDouble("yaw", 0.0);
            final float pitch = (float) warpSec.getDouble("pitch", 0.0);

            final Location loc = new Location(PLUGIN.getServer().getWorld(worldName), x, y, z, yaw, pitch);

            final Warp warp = new Warp(name, loc);

            warp.setDescription(warpSec.getString("description", ""));
            warp.setGroupRequired(warpSec.getString("group-required", ""));

            createWarp(warpID, warp);
            loaded++;

            LOGGER.info("Loaded warp '%s' at %s (%.2f, %.2f, %.2f)".formatted(warpID, worldName, x, y, z));
        }

        LOGGER.info("Finished loading warps: %d loaded, %d skipped.".formatted(loaded, skipped));
    }

    public void save() {
        final MainConfig config = PLUGIN.getMainConfig();

        config.createSection("warps");

        warps.forEach((id, warp) -> {
            final ConfigurationSection sec = config.createSection("warps." + id);

            sec.set("name", warp.getName());
            sec.set("world", warp.getWorldName());
            sec.set("x", warp.getX());
            sec.set("y", warp.getY());
            sec.set("z", warp.getZ());
            sec.set("yaw", warp.getYaw());
            sec.set("pitch", warp.getPitch());
            if (!warp.getDescription().isEmpty()) sec.set("description", warp.getDescription());
            if (!warp.getGroupRequired().isEmpty()) sec.set("group-required", warp.getGroupRequired());
        });
    }

    public @NotNull CompletableFuture<TeleportReturnCode> teleport(@NotNull Player player, @NotNull Warp warp) {
        final Location location = warp.toLocation();
        if (location == null) return CompletableFuture.completedFuture(TeleportReturnCode.WORLD_NOT_AVAILABLE);
        return player.teleportAsync(location).thenApply(success -> success ? TeleportReturnCode.SUCCESS : TeleportReturnCode.FAILURE);
    }
}