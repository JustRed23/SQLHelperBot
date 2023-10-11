package dev.JustRed23.sqlbot;

import java.util.concurrent.TimeUnit;

public final class SessionTimer {

    private static final long MAX_SESSION_INACTIVE_TIME = TimeUnit.MINUTES.toMillis(5);

    private final SessionManager session;
    private long lastActiveTime = -1;
    private boolean active = false;

    SessionTimer(SessionManager session) {
        this.session = session;
    }

    public void start() {
        reset();
        active = true;
    }

    public void reset() {
        lastActiveTime = System.currentTimeMillis();
    }

    public void stop() {
        active = false;
        lastActiveTime = -1;
        session.sendMsgToBoundChannel(session.getUser().getAsMention() + ", your session was closed due to inactivity");
        session.closeSession();
    }

    void tick() {
        if (!active) return;
        if (System.currentTimeMillis() - lastActiveTime > MAX_SESSION_INACTIVE_TIME) {
            stop();
        }
    }
}
