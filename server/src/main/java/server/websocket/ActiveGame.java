package server.websocket;

import chess.ChessGame;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveGame {
    Session white;
    String whiteUsername;
    Session black;
    String blackUsername;
    HashSet<Session> observers;

    public ActiveGame() {
        white = null;
        black = null;
        observers = new HashSet<>();
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

    private void announce(String message, ChessGame.TeamColor from) {
        if (from == null) {
            trySend(white, message);
            trySend(black, message);
        } else {
            Session recipient = from == ChessGame.TeamColor.WHITE ? white : black;
            trySend(recipient, message);
        }

        observers.forEach(session -> {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean addObserver(Session observer) {
        observers.add(observer);
        return true;
    }

    public boolean removeObserver(Session observer) {
        observers.remove(observer);
        return observers.isEmpty() && black == null && white == null;
    }

    private boolean setWhite(Session white, String username) {
        if (this.white == null || !this.white.isOpen()) {
            this.white = white;
            whiteUsername = username;
            announce(username + " joined the game as white", ChessGame.TeamColor.WHITE);
            return true;
        }
        return false;
    }

    private boolean setBlack(Session black, String username) {
        if (this.black == null || !this.black.isOpen()) {
            this.black = black;
            blackUsername = username;
            announce(username + " joined the game as black", ChessGame.TeamColor.BLACK);
            return true;
        }
        return false;
    }

    public boolean unsetWhite() {
        white = null;
        if (this.black == null && observers.isEmpty()) {
            return true;
        } else {
            announce(whiteUsername + " left the game", ChessGame.TeamColor.WHITE);
            return false;
        }
    }

    public boolean unsetBlack() {
        black = null;
        if (this.white == null && observers.isEmpty()) {
            return true;
        } else {
            announce(blackUsername + " left the game", ChessGame.TeamColor.BLACK);
            return false;
        }
    }

    public ActiveGame handleCommand(UserGameCommand command, Session session, String username, GameData game, AtomicReference<ServerMessage> res) {
        ChessGame.TeamColor team;
        if (username.equals(game.whiteUsername())) {
            team = ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            team = null;
        }

        switch (command.commandType()) {
            case CONNECT -> {
                boolean valid = switch (team) {
                    case WHITE -> setWhite(session, username);
                    case BLACK -> setBlack(session, username);
                    case null -> addObserver(session);
                };
                if (valid) {
                    res.set(new ServerMessage(game.game()));
                } else {
                    res.set(new ServerMessage("Already taken", true));
                }
            }
            case MAKE_MOVE -> {
            }
            case LEAVE -> {
            }
            case RESIGN -> {
            }
        }
        return this;
    }
}
