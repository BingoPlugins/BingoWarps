package com.bingoplugins.bwarps;

import com.bingoplugins.bwarps.api.API;
import com.bingoplugins.bwarps.api.returncodes.TeleportReturnCode;
import com.bingoplugins.bwarps.api.returncodes.WarpOperationsReturnCode;
import com.bingoplugins.bwarps.api.warps.Warp;
import com.bingoplugins.bwarps.config.MainConfig;
import com.bingoplugins.bwarps.warps.WarpManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin implements API {
    public static Plugin PLUGIN;
    public static Logger LOGGER;

    @Getter private MainConfig mainConfig;
    @Getter private WarpManager warpManager;

    @Override
    public void onEnable() {
        PLUGIN = this;
        LOGGER = getLogger();

        mainConfig = new MainConfig();
        warpManager = new WarpManager();

        warpManager.load();

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            warpManager.save();
            mainConfig.saveConfig();
        }, 200L, 200L);

        Bukkit.getServicesManager().register(API.class, this, this, ServicePriority.Highest);
    }

    @Override
    public void onDisable() {
        mainConfig.saveConfig();
    }

    public void disableSelf() {
        getServer().getPluginManager().disablePlugin(this);
    }

    @Override
    public @NotNull Optional<Warp> getWarp(@NotNull String warpID) {
        return warpManager.getWarp(warpID);
    }

    @Override
    public @NotNull WarpOperationsReturnCode createWarp(@NotNull String warpID, @NotNull Warp warp) {
        return warpManager.createWarp(warpID, warp);
    }

    @Override
    public @NotNull WarpOperationsReturnCode deleteWarp(@NotNull String warpID) {
        return warpManager.deleteWarp(warpID);
    }

    @Override
    public @NotNull Set<String> getWarpIDs() {
        return warpManager.getWarpIDs();
    }

    @Override
    public @NotNull CompletableFuture<TeleportReturnCode> teleport(@NotNull Player player, @NotNull Warp warp) {
        return warpManager.teleport(player, warp);
    }
}