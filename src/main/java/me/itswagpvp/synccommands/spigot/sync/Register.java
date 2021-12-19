package me.itswagpvp.synccommands.spigot.sync;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Register {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void createTable() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "CREATE TABLE IF NOT EXISTS SyncServers (serverName VARCHAR(256) NOT NULL)";

            try {
                PreparedStatement stmt = plugin.getHikari().getConnection().prepareStatement(sql);
                stmt.execute();
            } catch (SQLException e) {

                if (!e.getMessage().contains("already exists") || !e.getMessage().contains("doesn't exist")) e.printStackTrace();

            }
        });
    }

    public void registerServer() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String sql = "REPLACE INTO SyncServers (serverName) VALUES(?)";
            PreparedStatement stmt;

            try {
                stmt = plugin.getHikari().getConnection().prepareStatement(sql);

                stmt.setString(1, plugin.getConfig().getString("ServerName"));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void unregisterServer(String serverName) {
        plugin.sendConsoleMessage("&f-> &7Unregistered server name: &c" + serverName);
        PreparedStatement stmt;
        try (
                Connection connection = plugin.getHikari().getConnection()
                ){
            String sql = "DELETE FROM `SyncServers` WHERE `serverName` = '" + serverName + "'";
            stmt = connection.prepareStatement(sql);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getServerList() {
        CompletableFuture<List<String>> getList = CompletableFuture.supplyAsync(() -> {

            List<String> list = new ArrayList<>();
            try (
                    Connection connection = plugin.getHikari().getConnection();
                    PreparedStatement ps = connection.prepareStatement("SELECT serverName FROM SyncServers");
                    ResultSet rs = ps.executeQuery()
            ) {

                while (rs.next()) {
                    if (rs.getString("serverName").equals(plugin.getServerName())) {
                        continue;
                    }

                    list.add(rs.getString("serverName"));
                }

                return list;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return list;
        });

        try {
            return getList.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
