package me.itswagpvp.synccommands.general.updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Updater {

    public String getNewerVersion() {

        CompletableFuture<String> getVersion = CompletableFuture.supplyAsync(() -> {
            try {
                URL uri= new URL("https://dev.itswagpvp.eu/api/SyncCommands/version.html");
                URLConnection ec = uri.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        ec.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null)
                    a.append(inputLine);
                in.close();

                return a.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";

        });

        try {
            return getVersion.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return "";
    }

    public boolean isPluginOutdated(String currentVersion) {
        return !getNewerVersion().equals(currentVersion);
    }
}
