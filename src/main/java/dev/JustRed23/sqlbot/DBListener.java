package dev.JustRed23.sqlbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class DBListener extends ListenerAdapter {

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild() || event.getAuthor().isSystem())
            return;

        if (!SessionManager.hasSession(event.getMember()))
            return;

        SessionManager session = SessionManager.getOrCreate(event.getMember());
        if (!session.isSessionOpen() || !session.isBound())
            return;

        String message = event.getMessage().getContentRaw();
        if (message.startsWith("> ")) {
            if (event.getGuild().getIdLong() == session.getBoundGuildId()
                    && event.getChannel().getIdLong() == session.getBoundChannelId()) {
                String command = message.substring(2);
                session.sendMsgToProcess(command);
            }
        }
    }
}
