package com.bingoplugins.bwarps.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static com.bingoplugins.bwarps.Plugin.LOGGER;
import static com.bingoplugins.bwarps.Plugin.PLUGIN;

public abstract class Config extends YamlConfiguration {
    @NotNull
    private final File file;

    public Config(@NotNull String name) {
        file = new File(PLUGIN.getDataFolder(), name + ".yml");
        file.getParentFile().mkdirs();
        loadFile();
    }

    private void loadFile() {
        try {
            if (!file.exists()) file.createNewFile();
            load(file);
            if (!isConfigValid()) PLUGIN.disableSelf();
            else LOGGER.info("Loaded " + file.getName());
        } catch (IOException | InvalidConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Could not load config " + file.getName(), e);
            PLUGIN.disableSelf();
        }
    }

    public void saveConfig() {
        try { save(file); }
        catch (IOException e) { LOGGER.log(Level.SEVERE, "Could not save config " + file.getName(), e); }
    }

    public void reload() {
        loadFile();

    }

    protected abstract boolean isConfigValid();
}