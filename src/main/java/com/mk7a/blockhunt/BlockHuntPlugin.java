package com.mk7a.blockhunt;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class BlockHuntPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static final String COOLDOWN_CONFIG_PATH = "cooldown";

    private Economy economy = null;

    private File cooldownFile;
    private FileConfiguration cooldownFileConfig;

    private File blocksFile;
    private FileConfiguration blocksFileConfig;

    private long cooldownDuration;

    protected static final String prefix = Util.color("&8[&eBlockHunt&8] &7");

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            log.severe(prefix + "Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupDataFolder();
        setupConfigFile();
        setupBlocksFile();
        setupCooldownFile();
        setupCooldownDuration();

        new BlockHuntCommands(this);
        new InteractListener(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    saveBlocksConfig();
                    saveCooldownFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, 0, 20 * 60 * 2);

    }

    protected void saveBlocksConfig() throws IOException {
        blocksFileConfig.save(blocksFile);
    }

    protected void saveCooldownFile() throws IOException {
        cooldownFileConfig.save(cooldownFile);
    }


    private void setupDataFolder() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
    }


    private void setupConfigFile() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }


    private void setupCooldownDuration() {
        double configValue = getConfig().getDouble(COOLDOWN_CONFIG_PATH);
        cooldownDuration = (long) (configValue * 3600000);
    }


    private void setupCooldownFile() {

        cooldownFile = new File(getDataFolder(), "cooldowns.yml");
        cooldownFileConfig = loadYamlConfig(cooldownFile);
    }

    private void setupBlocksFile() {

        blocksFile = new File(getDataFolder(), "blocks.yml");
        blocksFileConfig = loadYamlConfig(blocksFile);

    }

    private FileConfiguration loadYamlConfig(File file) {

        FileConfiguration config = null;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = new YamlConfiguration();
            config.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        return config;
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }


    public Economy getEconomy() {
        return economy;
    }

    public long getCooldownDuration() {
        return cooldownDuration;
    }

    public FileConfiguration getCooldownFileConfig() {
        return cooldownFileConfig;
    }

    public FileConfiguration getBlocksFileConfig() {
        return blocksFileConfig;
    }
}
