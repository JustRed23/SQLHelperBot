package dev.JustRed23.sqlbot.services;

import dev.JustRed23.sqlbot.App;
import dev.JustRed23.sqlbot.BotConfig;
import dev.JustRed23.sqlbot.utils.JarUtils;
import dev.JustRed23.stonebrick.net.NetworkManager;
import dev.JustRed23.stonebrick.service.Service;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class UpdateService extends Service {

    public boolean shouldRun() {
        return BotConfig.enabled;
    }

    public long delayBetweenRuns() {
        return TimeUnit.MINUTES.toMillis(5);
    }

    public void run() throws Exception {
        final JSONObject json = NetworkManager.get("https://api.github.com/repos/JustRed23/SQLHelperBot/releases/latest")
                .blocking()
                .asJSONObject();

        final String latestTag = json.getString("tag_name");

        if (!App.version.gitHash().equals(latestTag)) {
            LOGGER.info("New version available: " + latestTag);

            NetworkManager.get(json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"))
                    .blocking()
                    .asFile(JarUtils.getJarFile());

            LOGGER.info("Updated to version " + latestTag);
            System.exit(-2); // Pterodactyl panel detects this as a crash and will automatically try a restart
        }
    }
}
