package me.itswagpvp.synccommands.bungeecord.utils;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;
import me.itswagpvp.synccommands.bungeecord.firedEvents.ReceivedCommandEvent;
import me.itswagpvp.synccommands.general.utils.InvalidCommandException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Checker {

    private static SyncCommandsBungee plugin;

    public static Thread loop;

    public Checker(SyncCommandsBungee plugin) {
        Checker.plugin = plugin;
    }

    public void checkNewCommands() {
        int refreshRate = (plugin.getConfig().getInt("Refresh-Rate") * 1000);
        loop = new Thread(() -> {
            while (true) {

                try (
                        Connection connection = plugin.getHikari().getConnection();
                        PreparedStatement ps =
                                connection.prepareStatement("SELECT * FROM SyncCommands WHERE serverName = '" +
                                        plugin.getServerName() + "';")
                ) {

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {

                        int id = 0;
                        try {
                            id = rs.getInt("id");
                            String sender = rs.getString("sender");
                            String command = rs.getString("command");

                            ReceivedCommandEvent event = new ReceivedCommandEvent(id, command, sender);
                            plugin.getProxy().getPluginManager().callEvent(event);

                            if (plugin.debugMode) {
                                plugin.sendConsoleMessage("[SyncCommands] Received command with ID = " + id + " from " + sender);
                            }

                            plugin.getProxy().getPluginManager().dispatchCommand(
                                    plugin.getProxy().getConsole(), command);
                            deleteRow(id);
                        } catch (Exception e) {
                            throw new InvalidCommandException("Received invalid command with ID: " + id, e);
                        }

                    }

                } catch (SQLException | InvalidCommandException throwables) {
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
        try (
                Connection connection = plugin.getHikari().getConnection()
                ){
            stmt = connection.prepareStatement("DELETE FROM `SyncCommands` WHERE `id` = " + id + ";");
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
