package me.itswagpvp.synccommands.spigot.utils;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesUtils {
    private static final SyncCommands plugin = SyncCommands.getInstance();
    private File messagesFile;
    private FileConfiguration messagesConfig;

    //TODO Fix this is null
    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public void reloadMessagesConfig() {
        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void createMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
