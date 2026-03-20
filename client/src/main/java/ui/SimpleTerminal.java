package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ServerException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.*;

public class SimpleTerminal implements ChessTerminal {
    Terminal terminal;
    LineReader reader;
    Map<String, Command> commands;
    String prompt;
    boolean dummy;
    boolean running;
    boolean prompting;

    public SimpleTerminal(boolean dummy) throws IOException {
        this.dummy = dummy;
        prompt = "> ";
    }

    private Collection<String> getBaseCommands() {
        if (prompting) {
            return commands == null ? List.of() : commands.keySet();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void displayHelp() {
        for (Command command : commands.values()) {
            StringBuilder line = new StringBuilder();
            line.append("  ").append(command.getName());
            for (String arg : command.getRequiredArguments()) {
                line.append(" [").append(arg).append("]");
            }
            for (String arg : command.getExtraArguments()) {
                line.append(" <").append(arg).append(">");
            }
            line.append(": ").append(command.getDescription());
            displayInfo(line.toString());
        }
    }

    @Override
    public String prompt(String prompt, boolean hidden) {
        if (hidden) {
            return reader.readLine(prompt, '*');
        } else {
            return reader.readLine(prompt);
        }
    }

    @Override
    public void showBoard(ChessBoard board, ChessGame.TeamColor side) {
        displayInfo("TODO");
    }

    @Override
    public void showGame(ChessGame game, ChessGame.TeamColor side) {
        displayInfo("TODO");
    }

    @Override
    public void displayError(String message) {
        if (dummy) {
            displayInfo(message);
        } else {
            terminal.writer().println(EscapeSequences.SET_TEXT_COLOR_RED+message+EscapeSequences.RESET_TEXT_COLOR);
            terminal.flush();
        }
    }

    @Override
    public void displayInfo(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    @Override
    public void setCommands(List<Command> commands) {
        this.commands = new HashMap<>(commands.size());
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    @Override
    public void loop() {
        TerminalBuilder builder = TerminalBuilder.builder();
        if (dummy) {
            builder.dumb(true);
        } else {
            builder.system(true);
        }
        try(Terminal term = builder.build()) {
            terminal = term;
            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter(this::getBaseCommands))
                    .build();
            running = true;
            while (running) {
                prompting = true;
                String line = reader.readLine(prompt).strip().toLowerCase();
                prompting = false;
                String[] argv = line.split(" ");

                Command command = commands.get(argv[0]);
                if (command == null) {
                    displayError("Invalid command: " + argv[0]);
                    continue;
                } else if (argv.length <= command.getRequiredArguments().size()) {
                    displayError("Not enough arguments. Expected "
                            + command.getRequiredArguments().size()
                            + " arguments, got "
                            + (argv.length - 1));
                    continue;
                }
                HashMap<String, String> args = genArguments(command, argv);
                try {
                    if (command.execute(args)) {
                        break;
                    }
                } catch (ServerException e) {
                    displayError(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        }
        terminal = null;
        reader = null;
    }

    private static HashMap<String, String> genArguments(Command command, String[] argv) {
        HashMap<String, String> args = new HashMap<>();
        List<String> requiredArguments = command.getRequiredArguments();
        int i;
        for (i = 1; i <= requiredArguments.size(); i++) {
            args.put(requiredArguments.get(i-1), argv[i]);
        }
        List<String> extraArguments = command.getExtraArguments();
        int end = Math.min(requiredArguments.size() + extraArguments.size(), argv.length-1);
        for (; i <= end; i++) {
            args.put(extraArguments.get(i-1), argv[i]);
        }
        return args;
    }
}
