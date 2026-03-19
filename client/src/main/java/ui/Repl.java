package ui;

import client.ServerException;
import client.ServerFacade;
import model.AuthData;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
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

    private Collection<String> getBaseCommands() {
        if (auth == null) {
            return List.of("help", "quit", "login", "register");
        } else {
            return List.of("help", "quit", "logout");
        }
    }

    private void login() {
        while (true) {
            String username = reader.readLine("Username: ");
            String password = reader.readLine("Password: ", '*');
            try {
                server.login(username, password);
            } catch (ServerException e) {
                terminal.writer().println(e.getMessage());
                if (e.getHttpCode() == 401) {
                    continue;
                }
            }
            break;
        }
    }

    private void register() {
        while (true) {
            String username = reader.readLine("Username: ");
            String email = reader.readLine("Email: ");
            String password = reader.readLine("Password: ", '*');
            try {
                server.register(username, password, email);
            } catch (ServerException e) {
                terminal.writer().println(e.getMessage());
                if (e.getHttpCode() == 403) {
                    continue;
                }
            }
            break;
        }
    }

    private boolean unauthenticated(String command) {
        switch (command) {
            case "login" -> login();
            case "register" -> register();
            default -> { return true; }
        }
        return false;
    }

    public void start() {
        try(Terminal term = TerminalBuilder.builder().system(true).build()) {
            terminal = term;
            reader = LineReaderBuilder.builder().terminal(terminal).build();

            while (true) {
                String line = reader.readLine("[Chess]> ");

                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }

                if ("login".equalsIgnoreCase(line)) {
                    login();
                }

                terminal.writer().println("Hello "+line);
                terminal.flush();
            }
        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        }
        terminal = null;
        reader = null;
    }
}
