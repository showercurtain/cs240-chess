package ui;

import chess.*;
import client.ServerException;
import client.ServerFacade;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Repl {
    AuthData auth;
    ServerFacade server;
    ChessTerminal terminal;
    List<GameData> games;

    final Object lock = new Object();
    int gameID;
    ChessGame state;
    boolean playing;
    ChessGame.TeamColor team;

    public Repl(ServerFacade server) throws IOException {
        auth = null;
        this.server = server;
        this.terminal = new SimpleConsole();
        //this.terminal = new FancyConsole(false);
        terminal.setPrompt("Chess> ");
        terminal.setCommands(getUnauthenticatedCommands());
        this.games = Collections.emptyList();
    }

    private void setAuth(AuthData auth) {
        this.auth = auth;
        if (auth == null) {
            terminal.setPrompt("Chess> ");
            terminal.setCommands(getUnauthenticatedCommands());
        } else {
            terminal.setPrompt("Chess [" + auth.username() + "]> ");
            terminal.setCommands(getAuthenticatedCommands());
        }
    }

    private void login(Map<String, String> args) throws ServerException {
        String username = args.get("username");
        if (username == null) {
            username = terminal.prompt("Username: ", false);
        }
        String password = terminal.prompt("Password: ", true);
        setAuth(server.login(username, password));
    }

    private void register(Map<String, String> args) throws ServerException {
        String username = args.get("username");
        if (username == null) {
            username = terminal.prompt("Username: ", false);
        }
        String email = args.get("email");
        if (email == null) {
            email = terminal.prompt("Email: ", false);
        }
        String password = terminal.prompt("Password: ", true);
        while (!password.equals(terminal.prompt("Confirm: ", true))) {
            terminal.displayError("Passwords do not match");
            password = terminal.prompt("Password: ", true);
        }
        setAuth(server.register(username, password, email));
    }

    private void logout() throws ServerException {
        server.logout(auth);
        setAuth(null);
    }

    private void clear() throws ServerException {
        server.clear();
        setAuth(null);
    }

    private void listGames() throws ServerException {
        games = server.listGames(auth).stream().toList();
        if (games.isEmpty()) {
            terminal.displayInfo("No games yet!");
            return;
        }
        int[] ids = new int[games.size()];
        int i = 0;
        for (GameData game : games) {
            ids[i] = game.gameID();
            i += 1;
        }
        int maxLength = Integer.toString(Arrays.stream(ids).max().getAsInt()).length();
        for (GameData game : games) {
            String id = Integer.toString(game.gameID());
            terminal.displayInfo(String.format(" %" + (maxLength - id.length() + 1) + "s: %s", id, game.gameName()));
        }
    }

    private Collection<String> getValidGameIds(Map<String, String> args) {
        ArrayList<String> ids = new ArrayList<>(games.size());
        for (GameData game : games) {
            ids.add(Integer.toString(game.gameID()));
        }
        return ids;
    }

    private GameData getGame(Map<String, String> args) {
        String gameId = args.get("gameID");
        int id;
        try {
            id = Integer.parseInt(gameId);
        } catch (NumberFormatException e) {
            terminal.displayError("Invalid gameID "+gameId);
            return null;
        }
        return games.get(id-1);
    }

    private void joinGame(Map<String, String> args) throws ServerException {
        model.GameData game = getGame(args);
        if (game == null) {
            return;
        }
        String side = args.get("side");
        ChessGame.TeamColor color = switch (side.toLowerCase()) {
            case "white" -> ChessGame.TeamColor.WHITE;
            case "black" -> ChessGame.TeamColor.BLACK;
            default -> {
                terminal.displayError("Invalid side " + side);
                terminal.displayError("Side must be white or black");
                yield null;
            }
        };
        if (color == null) {
            return;
        }
        server.joinGame(auth, color, game.gameID());
        synchronized (lock) {
            gameID = game.gameID();
            state = game.game();
            terminal.displayInfo("Joined successfully!");
            terminal.showGame(new GameData(0,
                    color == ChessGame.TeamColor.WHITE ? auth.username() : game.whiteUsername(),
                    color == ChessGame.TeamColor.BLACK ? auth.username() : game.blackUsername(),
                    game.gameName(),
                    game.game()), color);
            server.connectWebsocket(this::handleServerMessage);
            server.wsCommand(UserGameCommand.CommandType.CONNECT, auth, gameID, null);
            playing = true;
            team = color;
        }

        terminal.setCommands(getInGameCommands());
        terminal.setPrompt(game.gameName() + "> ");
    }

    private Collection<String> getValidSides(Map<String, String> args) {
        GameData game = getGame(args);
        if (game == null) {
            return List.of("white", "black");
        }
        ArrayList<String> sides = new ArrayList<>(2);
        if (game.whiteUsername() == null) {
            sides.add("white");
        }
        if (game.blackUsername() == null) {
            sides.add("black");
        }
        return sides;
    }

    private void createGame(Map<String, String> args) throws ServerException {
        String gameName = args.get("name");
        if (gameName == null) {
            gameName = terminal.prompt("Game name: ", false);
        }
        int id = server.createGame(gameName, auth);
        terminal.displayInfo("Created game " + gameName + " with id " + id);
    }

    private void observeGame(Map<String, String> args) throws ServerException {
        GameData game = getGame(args);
        if (game == null) {
            return;
        }

        synchronized (lock) {
            gameID = game.gameID();
            state = game.game();
            terminal.displayInfo("Joined successfully!");
            server.connectWebsocket(this::handleServerMessage);
            server.wsCommand(UserGameCommand.CommandType.CONNECT, auth, gameID, null);
            terminal.showGame(game, ChessGame.TeamColor.WHITE);
            playing = false;
            team = ChessGame.TeamColor.WHITE;
        }

        terminal.setCommands(getObserverCommands());
        terminal.setPrompt(game.gameName() + "> ");
    }

    private void handleServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                synchronized (lock) {
                    if (state == null) {
                        return;
                    }
                    state = message.game().orElse(state);
                    terminal.showBoard(state, team, null);
                }
            }
            case ERROR -> {
                terminal.displayError(message.errorMessage().orElse("Unknown server error"));
            }
            case NOTIFICATION -> {
                terminal.displayInfo(message.message().orElse("The server is broken :D"));
            }
        }
    }

    private void leaveGame() throws ServerException {
        synchronized (lock) {
            if (state == null) {
                terminal.displayError("Not in a game");
                return;
            }
            server.wsCommand(UserGameCommand.CommandType.LEAVE, auth, gameID, null);
            server.closeWebsocket();
            state = null;
        }
        games = Collections.emptyList();
        terminal.setCommands(getAuthenticatedCommands());
        terminal.setPrompt("Chess [" + auth.username() + "]> ");
    }

    private void redrawBoard() {
        terminal.showBoard(state, team, null);
    }

    private Collection<String> getValidHighlights(Map<String, String> args) {
        return state.getBoard().getPieces().keySet().stream().map(ChessPosition::toString).collect(Collectors.toSet());
    }

    private void highlight(Map<String, String> args) {
        String pos = args.get("position");
        ChessPosition position = ChessPosition.fromString(pos);
        terminal.showBoard(state, team, position);
    }

    private void resign() throws ServerException {
        server.wsCommand(UserGameCommand.CommandType.RESIGN, auth, gameID, null);
    }

    private List<Command> getUnauthenticatedCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(() -> true, "exit", "Closes the application"),
                Command.makeCommand(this::login, "login", "Log into Chess",
                        Collections.emptyList(), List.of("username")),
                Command.makeCommand(this::register, "register", "Create an account",
                        Collections.emptyList(), List.of("username", "email")),
                Command.makeCommand(this::clear, "clear", "DEBUG: Clear the database")
        );
    }

    private List<Command> getAuthenticatedCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(() -> true, "exit", "Closes the application"),
                Command.makeCommand(this::logout, "logout", "Log out of Chess"),
                Command.makeCommand(this::listGames, "list", "List all games"),
                Command.makeCommand(this::joinGame, "join", "Join a game",
                        List.of("gameID", "side"), Collections.emptyList(),
                        Map.of("side", this::getValidSides, "gameID", this::getValidGameIds)),
                Command.makeCommand(this::createGame, "create", "Create a game",
                        Collections.emptyList(), List.of("name")),
                Command.makeCommand(this::observeGame, "observe", "Observe a game",
                        List.of("gameID"), Collections.emptyList(),
                        Map.of("gameID", this::getValidGameIds))
        );
    }

    private List<Command> getInGameCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(this::redrawBoard, "redraw", "Redraws the current chess board"),
                Command.makeCommand(this::leaveGame, "leave", "Leave the game you are in"),
                Command.makeCommand(this::highlight, "highlight", "Highlight valid moves for a piece",
                        List.of("position"), Collections.emptyList(),
                        Map.of("position", this::getValidHighlights)),
                Command.makeCommand(this::resign, "resign", "Resign the game")
        );
    }

    private List<Command> getObserverCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(this::redrawBoard, "redraw", "Redraws the current chess board"),
                Command.makeCommand(this::highlight, "highlight", "Highlight valid moves for a piece",
                        List.of("position"), Collections.emptyList(),
                        Map.of("position", this::getValidHighlights)),
                Command.makeCommand(this::leaveGame, "leave", "Leave the game you are observing")
        );
    }

    public void start() {
        terminal.loop();
    }
}