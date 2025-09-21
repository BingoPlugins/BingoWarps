package com.bingoplugins.bwarps.api;

import com.bingoplugins.bwarps.api.returncodes.TeleportReturnCode;
import com.bingoplugins.bwarps.api.returncodes.WarpOperationsReturnCode;
import com.bingoplugins.bwarps.api.warps.Warp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface API {
    static API get() {
        return Bukkit.getServicesManager().getRegistration(API.class).getProvider();
    }

    @NotNull Optional<Warp> getWarp(@NotNull String warpID);
    @NotNull WarpOperationsReturnCode createWarp(@NotNull String warpID, @NotNull Warp warp);
    @NotNull WarpOperationsReturnCode deleteWarp(@NotNull String warpID);
    @NotNull Set<String> getWarpIDs();
    @NotNull CompletableFuture<TeleportReturnCode> teleport(@NotNull Player player, @NotNull Warp warp);
}