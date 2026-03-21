package ui;

import chess.*;
import client.ServerException;
import client.ServerFacade;
import model.AuthData;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Repl {
    AuthData auth;
    ServerFacade server;
    ChessTerminal terminal;

    public Repl(ServerFacade server) throws IOException {
        auth = null;
        this.server = server;
        this.terminal = new SimpleConsole();
        //this.terminal = new FancyConsole(false);
        terminal.setPrompt("Chess> ");
        terminal.setCommands(getUnauthenticatedCommands());
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
        String password = terminal.prompt("Password: ", true);;
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

    private void test(Map<String, String> args) {
        String arg = args.get("color");
        ChessGame.TeamColor color = arg == null || !arg.equals("black") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        terminal.showBoard(board, color);
    }

    private List<Command> getUnauthenticatedCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(() -> true, "exit", "Closes the application"),
                Command.makeCommand(this::login, "login", "Log into Chess",
                        Collections.emptyList(), List.of("username")),
                Command.makeCommand(this::register, "register", "Create an account",
                        Collections.emptyList(), List.of("username", "email")),
                Command.makeCommand(this::clear, "clear", "DEBUG: Clear the database"),
                Command.makeCommand(this::test, "test", "DEBUG: Print a test board",
                        Collections.emptyList(), List.of("color"))
        );
    }

    private List<Command> getAuthenticatedCommands() {
        return List.of(
                Command.makeCommand(terminal::displayHelp, "help", "Display this help menu"),
                Command.makeCommand(() -> true, "exit", "Closes the application"),
                Command.makeCommand(this::logout, "logout", "Log out of Chess")
        );
    }

    public void start() {
        terminal.loop();
    }
}
