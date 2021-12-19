package me.itswagpvp.synccommands.spigot.sync;

import me.itswagpvp.synccommands.general.utils.InvalidCommandException;
import me.itswagpvp.synccommands.spigot.SyncCommands;
import me.itswagpvp.synccommands.spigot.firedEvents.ReceivedCommandEvent;
import me.itswagpvp.synccommands.spigot.log.ConsoleLogger;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Checker {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void checkNewCommands() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {

            if (!plugin.getServer().getPluginManager().isPluginEnabled(plugin)) {return;}

            try (
                    Connection connection = plugin.getHikari().getConnection();
                    PreparedStatement ps =
                            connection.prepareStatement("SELECT * FROM SyncCommands WHERE serverName = '"+plugin.getServerName()+"';")
                    ){

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    int id = 0;
                    try {
                        id = rs.getInt("id");
                        String sender = rs.getString("sender");
                        String command = rs.getString("command");

                        ReceivedCommandEvent event = new ReceivedCommandEvent(id, command, sender);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getServer().getPluginManager().callEvent(event), 0);

                        new ConsoleLogger().logCommandReceived(id, command, sender);

                        Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command), 0);
                        deleteRow(id);
                    } catch (Exception e) {
                        throw new InvalidCommandException("Received invalid command with ID: " + id, e);
                    }

                }

            } catch (SQLException | InvalidCommandException throwables) {
                throwables.printStackTrace();
            }
        }, 0, (plugin.getConfig().getLong("Refresh-Rate", 60) * 20L));
    }

    private void deleteRow(int id) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PreparedStatement stmt;
            try (
                    Connection connection = plugin.getHikari().getConnection()
                    ){

                stmt = connection.prepareStatement("DELETE FROM `SyncCommands` WHERE `id` = " + id + ";");
                stmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
