package com.bingoplugins.bwarps;

import com.bingoplugins.bwarps.config.MainConfig;
import com.bingoplugins.bwarps.warps.WarpManager;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Plugin extends JavaPlugin {
    public static Plugin PLUGIN;
    public static Logger LOGGER;
    private static LuckPerms LP;

    @Getter private MainConfig mainConfig;
    @Getter private WarpManager warpManager;

    @Override
    public void onEnable() {
        PLUGIN = this;
        LOGGER = getLogger();

        try {
            LP = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
        } catch (NullPointerException e) {
            disableSelf();
            return;
        }

        mainConfig = new MainConfig();
        warpManager = new WarpManager();

        warpManager.load();

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            warpManager.save();
            mainConfig.saveConfig();
        }, 200L, 200L);
    }

    @Override
    public void onDisable() {
        mainConfig.saveConfig();
    }

    public void disableSelf() {
        getServer().getPluginManager().disablePlugin(this);
    }
}