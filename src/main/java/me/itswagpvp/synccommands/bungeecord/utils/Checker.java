package me.itswagpvp.synccommands.bungeecord.utils;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Checker {

    private static SyncCommandsBungee plugin;

    public Checker(SyncCommandsBungee plugin) {
        Checker.plugin = plugin;
    }

    public void checkNewCommands() {
        int refreshRate = (plugin.getConfig().getInt("Refresh-Rate") * 1000);
        Thread loop = new Thread(() -> {
            while (true) {

                try (
                        PreparedStatement ps =
                                new MySQL(plugin).getConnection().prepareStatement("SELECT * FROM SyncCommands WHERE serverName = '" +
                                        plugin.getServerName() + "';")
                ) {

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {

                        int id = rs.getInt("id");
                        String command = rs.getString("command");

                        plugin.getProxy().getPluginManager().dispatchCommand(
                                plugin.getProxy().getConsole(), command);
                        deleteRow(id);

                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                try {
                    Thread.sleep(refreshRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        loop.start();
    }


    private void deleteRow(int id) {

        PreparedStatement stmt;
        try {

            stmt = new MySQL(plugin).getConnection().prepareStatement("DELETE FROM `SyncCommands` WHERE `id` = " + id + ";");
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
