package ui;

import client.ServerException;
import client.ServerFacade;
import model.AuthData;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class Repl {
    AuthData auth;
    ServerFacade server;
    Terminal terminal;
    LineReader reader;

    public Repl(ServerFacade server) {
        auth = null;
        this.server = server;
    }

    private void showHelp() {
        if (auth == null) {
            terminal.writer().print("""
  help: Show this menu
  exit: Close the application
  login: Log into Chess
  register: Create an account
""");
        } else {
            terminal.writer().print("""
  help: Show this menu
  exit: Close the application
  logout: Log out of Chess
  TODO
""");
        }
    }

    private Collection<String> getBaseCommands() {
        if (auth == null) {
            return List.of("help", "exit", "clear", "login", "register");
        } else {
            return List.of("help", "exit", "clear", "logout");
        }
    }

    private void login() throws ServerException {
        String username = reader.readLine("Username: ");
        String password = reader.readLine("Password: ", '*');
        auth = server.login(username, password);
    }

    private void register() throws ServerException {
        String username = reader.readLine("Username: ");
        String email = reader.readLine("Email: ");
        String password = reader.readLine("Password: ", '*');
        auth = server.register(username, password, email);
    }

    private void logout() throws ServerException {
        server.logout(auth);
        auth = null;
    }

    private void clear() throws ServerException {
        server.clear();
        auth = null;
    }

    private boolean unauthenticated(String command) {
        try {
            switch (command) {
                case "login" -> login();
                case "register" -> register();
                case "clear" -> clear();
                default -> { return true; }
            }
        } catch (ServerException e) {
            terminal.writer().println(e.getMessage());
        }

        return false;
    }

    private boolean authenticated(String command) {
        try {
            switch (command) {
                case "logout" -> logout();
                case "clear" -> clear();
                default -> { return true; }
            }
        } catch (ServerException e) {
            terminal.writer().println(e.getMessage());
        }

        return false;
    }

    public void start() {
        try(Terminal term = TerminalBuilder.builder().system(true).build()) {
            terminal = term;
            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter(this::getBaseCommands))
                    .build();

            while (true) {
                String line;
                if (auth == null) {
                    line = reader.readLine("Chess> ").strip();
                } else {
                    line = reader.readLine("Chess [" + auth.username() + "]> ").strip();
                }

                if ("exit".equalsIgnoreCase(line)) {
                    if (auth != null) {
                        logout();
                    }
                    break;
                }

                if ("help".equalsIgnoreCase(line)) {
                    showHelp();
                    continue;
                }

                // Something feels so wrong about using a ternary in an if statement
                if (auth == null ? unauthenticated(line) : authenticated(line)) {
                    terminal.writer().println("Unknown command " + line);
                }

                terminal.flush();
            }
        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        } catch (ServerException e) {
            System.err.println(e.getMessage());
        }
        terminal = null;
        reader = null;
    }
}
