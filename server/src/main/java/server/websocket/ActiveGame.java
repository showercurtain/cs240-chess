package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import service.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;

public class ActiveGame {
    Session white;
    String whiteUsername;
    Session black;
    String blackUsername;
    Gson gson;
    GameDAO gameDAO;
    boolean finished;
    final HashSet<Session> observers;

    public ActiveGame(Gson gson, GameDAO gameDAO) {
        white = null;
        black = null;
        observers = new HashSet<>();
        finished = false;
        this.gson = gson;
        this.gameDAO = gameDAO;
    }

    private void finish() {
        this.finished = true;
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

    private void trySend(Session to, ServerMessage message) {
        trySend(to, gson.toJson(message));
    }

    private void updateDatabase(GameData gameData, Session feedback, boolean empty) {
        try {
            if (finished && empty) {
                gameDAO.removeGame(gameData.gameID());
            } else {
                gameDAO.updateGame(new GameData(
                        gameData.gameID(),
                        whiteUsername,
                        blackUsername,
                        gameData.gameName(),
                        gameData.game()
                ));
            }
        } catch (ServiceException e) {
            trySend(feedback, new ServerMessage(e.getMessage(), true));
        }
    }

    private void announce(ServerMessage message, ChessGame.TeamColor from) {
        String text = gson.toJson(message);
        if (from == null) {
            trySend(white, text);
            trySend(black, text);
        } else {
            Session recipient = from == ChessGame.TeamColor.WHITE ? black : white;
            trySend(recipient, text);
        }

        observers.forEach(session -> {
            trySend(session, text);
        });
    }

    private void announceObserver(ServerMessage message, Session from) {
        String text = gson.toJson(message);
        trySend(white, text);
        trySend(black, text);
        for (Session session : observers) {
            if (session != from) {
                trySend(session, text);
            }
        }
    }

    private boolean addObserver(Session observer, String username) {
        observers.add(observer);
        announceObserver(new ServerMessage(username + " is observing"), observer);
        return true;
    }

    public boolean removeObserver(Session observer, String username) {
        observers.remove(observer);
        announce(new ServerMessage(username + " left"), null);
        return observers.isEmpty() && black == null && white == null;
    }

    private boolean setWhite(Session white, String username) {
        if (this.white == null || !this.white.isOpen()) {
            this.white = white;
            whiteUsername = username;
            announce(new ServerMessage(username + " joined the game as white"), ChessGame.TeamColor.WHITE);
            return true;
        }
        return false;
    }

    private boolean setBlack(Session black, String username) {
        if (this.black == null || !this.black.isOpen()) {
            this.black = black;
            blackUsername = username;
            announce(new ServerMessage(username + " joined the game as black"), ChessGame.TeamColor.BLACK);
            return true;
        }
        return false;
    }

    public boolean unsetWhite() {
        white = null;
        String username = whiteUsername;
        whiteUsername = null;
        if (this.black == null && observers.isEmpty()) {
            return true;
        } else {
            announce(new ServerMessage(username + " left the game"), ChessGame.TeamColor.WHITE);
            return false;
        }
    }

    public boolean unsetBlack() {
        black = null;
        String username = blackUsername;
        blackUsername = null;
        if (this.white == null && observers.isEmpty()) {
            return true;
        } else {
            announce(new ServerMessage(username + " left the game"), ChessGame.TeamColor.BLACK);
            return false;
        }
    }

    public ActiveGame handleCommand(
            UserGameCommand command,
            Session session,
            String username,
            GameData game
    ) {
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
                    case null -> addObserver(session, username);
                };
                if (valid) {
                    trySend(session, new ServerMessage(game.game()));
                } else {
                    trySend(session, new ServerMessage("Already taken", true));
                }
            }
            case MAKE_MOVE -> {
                if (team == null) {
                    trySend(session, new ServerMessage("Observers can't move pieces!", true));
                    return this;
                }
                if (finished) {
                    trySend(session, new ServerMessage("The game has already ended", true));
                    return this;
                }
                if (team != game.game().getTeamTurn()) {
                    trySend(session, new ServerMessage("You cannot move when it isn't your turn", true));
                    return this;
                }
                ChessMove move = command.move();
                try {
                    game.game().makeMove(move);
                } catch (InvalidMoveException e) {
                    trySend(session, new ServerMessage(e.getMessage(), true));
                    return this;
                }
                announce(new ServerMessage(
                        ServerMessage.ServerMessageType.LOAD_GAME,
                        game.game(), null, null), null);
                announce(new ServerMessage(
                        username + " moved " + move.startPosition() + " to " + move.endPosition() +
                                (move.promotionPiece() == null ? "" : " and promoted their piece")), team);
                if (game.game().isInStalemate(team.getOpposite())) {
                    announce(new ServerMessage("Stalemate."), null);
                    finish();
                } else if (game.game().isInCheckmate(team.getOpposite())) {
                    announce(new ServerMessage("Checkmate."), null);
                    finish();
                } else if (game.game().isInCheck(team.getOpposite())) {
                    announce(new ServerMessage("Check."), null);
                }
                updateDatabase(game, session, false);

            }
            case LEAVE -> {
                boolean empty = switch (team) {
                    case WHITE -> unsetWhite();
                    case BLACK -> unsetBlack();
                    case null -> removeObserver(session, username);
                };
                updateDatabase(game, session, empty);
                if (empty) {
                    return null;
                }
            }
            case RESIGN -> {
                if (team == null) {
                    trySend(session, new ServerMessage("Observers can't resign!", true));
                    return this;
                }
                if (finished) {
                    trySend(session, new ServerMessage("The game has already ended", true));
                    return this;
                }
                finish();

                String opponent = switch (team) {
                    case WHITE -> blackUsername;
                    case BLACK -> whiteUsername;
                };
                announce(new ServerMessage(username + " resigned. " + opponent + " wins!"), null);
            }
        }
        return this;
    }
}
