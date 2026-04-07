package server;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveGame {
    final Object lock = new Object();
    Session white;
    Session black;
    ConcurrentHashMap<Session,Session> observers;

    public ActiveGame() {
        white = null;
        black = null;
        observers = new ConcurrentHashMap<>();
    }

    private static void trySend(Session to, String message) {
        if (to != null && to.isOpen()) {
            try {
                to.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void announce(String message, ChessGame.TeamColor from) {
        if (from == null) {
            synchronized (lock) {
                trySend(white, message);
                trySend(black, message);
            }
        } else {
            synchronized (lock) {
                Session recipient = from == ChessGame.TeamColor.WHITE ? white : black;
                trySend(recipient, message);
            }

        }

        observers.forEach((session, _session2) -> {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addObserver(Session observer) {
        observers.put(observer, observer);
    }

    public void removeObserver(Session observer) {
        observers.remove(observer);
    }

    public boolean setWhite(Session white) {
        synchronized (lock) {
            if (this.white == null) {
                this.white = white;
                return true;
            }
        }

        return false;
    }

    public boolean setBlack(Session black) {
        synchronized (lock) {
            if (this.black == null) {
                this.black = black;
                return true;
            }
        }

        return false;
    }

    public boolean unsetWhite() {
        synchronized (lock) {
            this.white = null;
            if (this.black == null && observers.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public boolean unsetBlack() {
        synchronized (lock) {
            this.black = null;
            if (this.white == null && observers.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
