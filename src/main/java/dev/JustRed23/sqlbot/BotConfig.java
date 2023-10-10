package dev.JustRed23.sqlbot;

import dev.JustRed23.abcm.ConfigField;
import dev.JustRed23.abcm.Configurable;

@Configurable
public class BotConfig {

    public static final long start_time = System.currentTimeMillis();

    @ConfigField(defaultValue = "false")
    public static boolean enabled;

    @ConfigField(defaultValue = "SQLHelper")
    public static String name;

    @ConfigField(defaultValue = "5730")
    public static String tag;

    @ConfigField(defaultValue = "https://github.com/JustRed23/SQLHelperBot")
    public static String website_url;

    @ConfigField(defaultValue = "")
    public static String token;

    @ConfigField(defaultValue = "253219587787718658")
    public static String owner_id;

    @ConfigField(defaultValue = "826438912535691295")
    public static String guild_id;
}
