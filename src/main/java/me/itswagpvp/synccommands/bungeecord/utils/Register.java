package me.itswagpvp.synccommands.bungeecord.utils;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Register {

    private static SyncCommandsBungee plugin;
    public Register(SyncCommandsBungee plugin) {
        Register.plugin = plugin;
    }

    public void createTable() {
        Thread createTable = new Thread(() -> {

            String sql = "CREATE TABLE SyncServers (serverName VARCHAR(256) NOT NULL)";

            try {
                PreparedStatement stmt = new MySQL(plugin).getConnection().prepareStatement(sql);
                stmt.execute();
            } catch (SQLException e) {

                if (!e.getMessage().contains("already exists")) e.printStackTrace();

            }
        });

        createTable.start();

    }

    public void registerServer() {
        Thread registerServer = new Thread(() -> {

            String sql = "REPLACE INTO SyncServers (serverName) VALUES(?)";
            PreparedStatement stmt;

            try {
                stmt = new MySQL(plugin).getConnection().prepareStatement(sql);

                stmt.setString(1, plugin.getConfig().getString("ServerName"));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        registerServer.start();

    }

    public void unregisterServer(String serverName) {
        Thread addCommand = new Thread(() -> {

            plugin.sendConsoleMessage("&f-> &7Unregistered server name: &c" + serverName);
            PreparedStatement stmt;
            try {
                String sql = "DELETE FROM `SyncServers` WHERE `serverName` = '" + serverName + "'";
                stmt = new MySQL(plugin).getConnection().prepareStatement(sql);
                stmt.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        addCommand.start();

    }

    public List<String> getServerList() {
        CompletableFuture<List<String>> getList = CompletableFuture.supplyAsync(() -> {

            List<String> list = new ArrayList<>();
            try (
                    PreparedStatement ps = new MySQL(plugin).getConnection().prepareStatement("SELECT serverName FROM SyncServers");
                    ResultSet rs = ps.executeQuery()
            ) {

                while (rs.next()) {
                    if (rs.getString("serverName") == plugin.getServerName()) {
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
