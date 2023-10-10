package dev.JustRed23.sqlbot;

import dev.JustRed23.abcm.Config;
import dev.JustRed23.jdautils.JDAUtilities;
import dev.JustRed23.stonebrick.app.Application;
import dev.JustRed23.stonebrick.data.FileStructure;
import dev.JustRed23.stonebrick.log.SBLogger;
import dev.JustRed23.stonebrick.version.GitVersion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import java.util.Arrays;

public class App extends Application {

    public static Logger LOGGER;
    public static GitVersion version;

    private static JDA instance;
    private JDABuilder builder;

    protected void init() {
        LOGGER = SBLogger.getLogger(BotConfig.name);
        version = GitVersion.fromFile(getClass().getClassLoader().getResourceAsStream("application.properties"));
        FileStructure.disable();

        builder = JDABuilder.createDefault(BotConfig.token)
                .setEnableShutdownHook(false)
                .setEnabledIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                )
                .disableCache(Arrays.stream(CacheFlag.values()).toList())
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.playing("with tables"));

        JDAUtilities.createSlashCommand("open", "Opens a new sqlcmd session (only one session at a time)")
                .addCondition(ctx -> {
                    if (SessionManager.isSessionOpen()) {
                        ctx.reply("A session is already open").queue();
                        return false;
                    }
                    return true;
                })
                .executes(ctx -> {
                    SessionManager.bindChannel(ctx.getChannel().asGuildMessageChannel());
                    boolean opened = SessionManager.openSession();
                    if (opened)
                        ctx.reply("Session opened").queue();
                    else
                        ctx.reply("Failed to open session").queue();
                })
                .buildAndRegister();

        JDAUtilities.createSlashCommand("close", "Closes the current sqlcmd session")
                .addCondition(ctx -> {
                    if (!SessionManager.isSessionOpen()) {
                        ctx.reply("No session is currently open").queue();
                        return false;
                    }
                    return true;
                })
                .executes(ctx -> {
                    ctx.deferReply().queue();
                    boolean closed = SessionManager.closeSession();
                    if (closed)
                        ctx.getHook().editOriginal("Session closed").queue();
                    else
                        ctx.getHook().editOriginal("Failed to close session").queue();
                })
                .buildAndRegister();
    }

    protected void start() throws Exception {
        if (!BotConfig.enabled) {
            LOGGER.info("Bot is disabled. Exiting...");
            exit();
            return;
        }

        instance = builder.build();
        instance.addEventListener(JDAUtilities.getInstance().listener(), new DBListener());
        instance.awaitReady();
    }

    protected void stop() {
        if (instance == null || !BotConfig.enabled)
            return;

        if (SessionManager.isSessionOpen())
            SessionManager.closeSession();

        instance.shutdown();
    }

    public static JDA getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Config.setDebug(true);
        launch(args);
    }
}
