package dev.JustRed23.sqlbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class SessionManager {

    private static long guildId = -1;
    private static long channelId = -1;

    private static boolean sessionOpen = false;
    private static Process sqlcmdProcess;
    private static BufferedWriter processWriter;
    private static Thread processListener, processSender;

    private static final List<String> sendQueue = Collections.synchronizedList(new ArrayList<>());
    private static final AtomicInteger waitTime = new AtomicInteger(0);

    private static boolean manualClose = false;

    public static boolean openSession() {
        if (!sessionOpen) {
            manualClose = false;
            ProcessBuilder builder = new ProcessBuilder(Commands.OPEN_SQLCMD.getCommand().split(" "));
            try {
                App.LOGGER.debug("Opening SQLCMD session with command: " + String.join(" ", builder.command()));
                sqlcmdProcess = builder.start();

                sqlcmdProcess.onExit().thenAccept(process -> {
                    if (process.exitValue() == 0) {
                        if (!manualClose) sendMsgToBoundChannel("Session closed");
                    } else sendMsgToBoundChannel("Session closed with error code " + process.exitValue());

                    sessionOpen = false;
                    guildId = -1;
                    channelId = -1;

                    App.LOGGER.debug("SQLCMD exit future completed");
                });

                createProcessListener();

                processWriter = new BufferedWriter(new OutputStreamWriter(sqlcmdProcess.getOutputStream()));

                App.LOGGER.debug("Opened SQLCMD session");
            } catch (IOException e) {
                sendMsgToBoundChannel("Failed to open session: " + e.getMessage());
                return false;
            }

            sessionOpen = true;
            return true;
        }
        return false;
    }

    public static boolean closeSession() {
        if (!sessionOpen) return false;

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

        sessionOpen = false;
        guildId = -1;
        channelId = -1;

        return true;
    }

    private static void sendMsgToBoundChannel(String msg) {
        if (!isBound()) return;

        final Guild guildById = App.getInstance().getGuildById(guildId);
        if (guildById == null) {
            App.LOGGER.error("Failed to send message to binded channel: guild not found");
            return;
        }

        final TextChannel channelById = guildById.getTextChannelById(channelId);
        if (channelById != null)
            channelById.sendMessage(msg).queue();
        else App.LOGGER.error("Failed to send message to binded channel: channel not found");
    }

    public static void sendMsgToProcess(String msg) {
        if (!sessionOpen) return;

        try {
            processWriter.write(msg);
            processWriter.newLine();
            processWriter.flush();

            App.LOGGER.debug("Sent message to process: " + msg);
        } catch (IOException e) {
            App.LOGGER.error("Failed to send message to process", e);
        }
    }

    private static void createProcessListener() {
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

    private static void addToSendQueue(String msg) {
        if (!sessionOpen) return;
        synchronized (sendQueue) {
            sendQueue.add(msg);
        }
        waitTime.set(1_000);
    }

    public static void bindChannel(TextChannel channel) {
        if (isBound()) return;
        channelId = channel.getIdLong();
        guildId = channel.getGuild().getIdLong();
    }

    public static boolean isSessionOpen() {
        return sessionOpen;
    }

    public static boolean isBound() {
        return guildId != -1 && channelId != -1;
    }

    public static long getBoundGuildId() {
        return guildId;
    }

    public static long getBoundChannelId() {
        return channelId;
    }
}
