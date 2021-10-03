package me.itswagpvp.synccommands.utils;

import me.itswagpvp.synccommands.SyncCommands;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Checker {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void checkNewCommands() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, () -> {
            try (
                    PreparedStatement ps =
                            new MySQL().getConnection().prepareStatement("SELECT * FROM SyncCommands WHERE serverName = '"+plugin.getServerName()+"';")
                    ){

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {

                    int id = rs.getInt("id");
                    String command = rs.getString("command");

                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), command), 0);
                    deleteRow(id);
                }


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }, 0, (plugin.getConfig().getLong("Refresh-Rate", 60) * 20L));
    }

    private void deleteRow(int id) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PreparedStatement stmt;
            try {

                stmt = new MySQL().getConnection().prepareStatement("DELETE FROM `SyncCommands` WHERE `id` = " + id + ";");
                stmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
