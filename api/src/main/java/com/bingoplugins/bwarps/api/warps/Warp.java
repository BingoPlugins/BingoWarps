package com.bingoplugins.bwarps.api.warps;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Warp {
    @Getter @Setter private volatile String name;
    @Getter @Setter private volatile String description = "";
    @Getter @Setter private volatile String groupRequired = "";

    @Getter private final String worldName;
    @Getter private final double x, y, z;
    @Getter private final float yaw, pitch;

    public Warp(@NotNull final String name, @NotNull final Location loc) {
        this.name = name;
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    @Nullable
    public Location toLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z, yaw, pitch);
    }
}