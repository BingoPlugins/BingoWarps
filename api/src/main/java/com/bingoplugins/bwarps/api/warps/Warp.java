package com.bingoplugins.bwarps.api.warps;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Warp {
    @Getter @Setter private volatile String description = "";
    private final Location location;

    public Warp(@NotNull final Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location.clone();
    }
}
