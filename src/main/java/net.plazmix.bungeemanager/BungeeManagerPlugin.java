package net.plazmix.bungeemanager;

import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.plazmix.bungeemanager.announer.AnnounceManager;
import net.plazmix.bungeemanager.command.PingCommand;
import net.plazmix.bungeemanager.command.WhitelistCommand;
import net.plazmix.bungeemanager.whitelist.WhitelistManager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Getter
public final class BungeeManagerPlugin extends Plugin {

    private Configuration config;

    private final WhitelistManager whitelistManager = new WhitelistManager();
    private final AnnounceManager announceManager   = new AnnounceManager();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        startAutoUpdater();

        whitelistManager.load(this);
        announceManager.startAnnounceTask(getConfig().getInt("announcer_period"), this);

        getProxy().getPluginManager().registerCommand(this, new PingCommand());
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand(this));

        getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
    }


    @SneakyThrows
    private void saveDefaultConfig() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        File configFile = getDataFolder().toPath().resolve("config.yml").toFile();

        if (configFile.exists()) {
            reloadConfig();
            return;
        }

        InputStream inputStream = getResourceAsStream("config.yml");
        if (inputStream == null)
            return;

        Files.copy(inputStream, configFile.toPath());
        reloadConfig();
    }

    @SneakyThrows
    public void reloadConfig() {
        File configFile = getDataFolder().toPath().resolve("config.yml").toFile();

        if (!configFile.exists())
            return;

        this.config = YamlConfiguration.getProvider(YamlConfiguration.class).load(configFile);
    }

    @SneakyThrows
    public void saveConfig() {
        if (config == null) {
            return;
        }

        File configFile = getDataFolder().toPath().resolve("config.yml").toFile();
        if (!configFile.exists())
            return;

        YamlConfiguration.getProvider(YamlConfiguration.class).save(config, configFile);
    }


    private void startAutoUpdater() {
        getProxy().getScheduler().schedule(this, () -> {

            reloadConfig();
            whitelistManager.load(this);

        }, 5, 5, TimeUnit.MINUTES);
    }

}
