package me.itswagpvp.synccommands.spigot.log;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public File logFile;
    public FileConfiguration logConfig;

    public void createLog(int commandId, String command, String sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[yyyy/MM/dd HH-mm-ss]");
            LocalDateTime now = LocalDateTime.now();

            createLogConfig();
            getLogConfig().set(dtf.format(now), "ID: " + commandId + " | Command: " + command + " | Sender: " + sender);
            try {
                getLogConfig().save(logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public FileConfiguration getLogConfig() {
        return logConfig;
    }

    public void createLogConfig() {
        logFile = new File(plugin.getDataFolder(), "log.txt");
        if (!logFile.exists()) {
            logFile.getParentFile().mkdirs();
            plugin.saveResource("log.txt", false);
        }

        logConfig = new YamlConfiguration();
        try {
            logConfig.load(logFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
