package dev.JustRed23.sqlbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class SessionManager {

    private static final Map<Member, SessionManager> sessions = Collections.synchronizedMap(new HashMap<>());

    public static SessionManager getOrCreate(Member user) {
        synchronized (sessions) {
            if (hasSession(user)) return sessions.get(user);
            SessionManager session = new SessionManager(user);
            sessions.put(user, session);
            return session;
        }
    }

    public static synchronized List<SessionManager> getSessions() {
        return sessions.values().stream().toList();
    }

    public static boolean hasSession(Member user) {
        return sessions.containsKey(user);
    }



    private final SessionTimer timer = new SessionTimer(this);
    private final List<String> sendQueue = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger waitTime = new AtomicInteger(0);

    private final Member user;

    private long guildId = -1;
    private long channelId = -1;

    private boolean sessionOpen = false;
    private boolean manualClose = false;
    private boolean isClosing = false;

    private Process sqlcmdProcess;
    private BufferedWriter processWriter;
    private Thread processListener, processSender;

    private SessionManager(Member user) {
        this.user = user;
    }

    public boolean openSession() {
        if (!sessionOpen) {
            manualClose = false;
            ProcessBuilder builder = new ProcessBuilder(BotConfig.sqlcmd.split(" "));
            try {
                App.LOGGER.debug("Opening SQLCMD session with command: " + String.join(" ", builder.command()));
                sqlcmdProcess = builder.start();

                sqlcmdProcess.onExit().thenAccept(process -> {
                    if (process.exitValue() == 0) {
                        if (!manualClose) sendMsgToBoundChannel("Session closed");
                    } else sendMsgToBoundChannel("Session closed with error code " + process.exitValue());

                    unset();

                    App.LOGGER.debug("SQLCMD exit future completed");
                });

                while (!sqlcmdProcess.isAlive()) {
                    Thread.sleep(100);
                }

                sessionOpen = true;

                processWriter = new BufferedWriter(new OutputStreamWriter(sqlcmdProcess.getOutputStream()));

                createProcessListener();

                timer.start();

                App.LOGGER.debug("Opened SQLCMD session");
            } catch (IOException e) {
                sendMsgToBoundChannel("Failed to open session: " + e.getMessage());
                return false;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    public boolean closeSession() {
        if (!sessionOpen || isClosing) return false;
        isClosing = true;

        manualClose = true;

        App.LOGGER.debug("Closing process");
        sendMsgToProcess("quit");
        try {
            if (!sqlcmdProcess.waitFor(60, TimeUnit.SECONDS)) {
                App.LOGGER.error("Failed to close process: timeout");
                return false;
            }
        } catch (InterruptedException e) {
            App.LOGGER.error("Failed to wait for process to close", e);
        }

        if (sqlcmdProcess.isAlive()) {
            App.LOGGER.error("Failed to close process");
            return false;
        }

        try {
            App.LOGGER.debug("Joining process threads");
            processListener.join(10_000);
            processSender.join(10_000);
        } catch (InterruptedException e) {
            App.LOGGER.error("Failed to join threads", e);
        }
        App.LOGGER.debug("Process threads joined");

        unset();

        isClosing = false;
        return true;
    }

    public void sendMsgToBoundChannel(String msg) {
        if (!isBound()) return;

        final Guild guildById = App.getInstance().getGuildById(guildId);
        if (guildById == null) {
            App.LOGGER.error("Failed to send message to binded channel: guild not found");
            return;
        }

        final GuildChannel channelById = guildById.getGuildChannelById(channelId);
        if (channelById instanceof GuildMessageChannel channel)
            channel.sendMessage(msg).queue(suc -> {}, err -> App.LOGGER.error("Failed to send message to binded channel", err));
        else App.LOGGER.error("Failed to send message to binded channel: channel not found");
    }

    public void sendMsgToProcess(String msg) {
        if (!sessionOpen) return;

        try {
            processWriter.write(msg);
            processWriter.newLine();
            processWriter.flush();

            timer.reset();

            App.LOGGER.debug("Sent message to process: " + msg);
        } catch (IOException e) {
            App.LOGGER.error("Failed to send message to process", e);
        }
    }

    private void createProcessListener() {
        processListener = new Thread(() ->
        {
            App.LOGGER.debug("Process listener thread started");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(sqlcmdProcess.getInputStream()))) {
                while (sessionOpen) {
                    String line = reader.readLine();
                    if (line == null) break;

                    App.LOGGER.debug("Received message from process: " + line);
                    addToSendQueue(line);
                }
            } catch (Exception e) {
                App.LOGGER.error("Failed to read from process", e);
                sendMsgToBoundChannel("Something went wrong while trying to read the output of the process, closing session");
                closeSession();
            }

            App.LOGGER.debug("Process listener thread stopped");
        }, "Process Listener");
        processListener.start();

        processSender = new Thread(() ->
        {
            App.LOGGER.debug("Process sender thread started");

            try {
                while (sessionOpen) {
                    if (waitTime.get() > 0) {
                        waitTime.decrementAndGet();
                        continue;
                    }

                    Thread.sleep(100);

                    if (waitTime.get() > 0) continue;

                    if (sendQueue.isEmpty()) continue;

                    List<String> snapshot = new ArrayList<>(sendQueue);

                    StringBuilder builder = new StringBuilder();
                    builder.append("```").append(System.lineSeparator());
                    for (String s : snapshot) {
                        if (s.isBlank()) s = System.lineSeparator();
                        builder.append(s).append(System.lineSeparator());
                    }
                    builder.append("```");

                    sendMsgToBoundChannel(builder.toString());

                    synchronized (sendQueue) {
                        sendQueue.removeAll(snapshot);
                    }
                }
            } catch (InterruptedException e) {
                App.LOGGER.error("Sender thread got interrupted", e);
            }

            App.LOGGER.debug("Process sender thread stopped");
        }, "Process Sender");
        processSender.start();
    }

    void tick() {
        timer.tick();
    }

    private void addToSendQueue(String msg) {
        if (!sessionOpen) return;
        synchronized (sendQueue) {
            sendQueue.add(msg);
        }
        waitTime.set(1_000);
    }

    public void bindChannel(GuildMessageChannel channel) {
        if (isBound()) return;
        channelId = channel.getIdLong();
        guildId = channel.getGuild().getIdLong();
        if (channel.getType().isThread())
            ((ThreadChannel) channel).join().queue();
    }

    private void unset() {
        sessionOpen = false;
        guildId = -1;
        channelId = -1;
        timer.stop();

        synchronized (sessions) {
            sessions.remove(user);
        }
    }

    public boolean isSessionOpen() {
        return sessionOpen;
    }

    public boolean isBound() {
        return guildId != -1 && channelId != -1;
    }

    public long getBoundGuildId() {
        return guildId;
    }

    public long getBoundChannelId() {
        return channelId;
    }

    public Member getUser() {
        return user;
    }
}
