package ui;

import chess.ChessBoard;
import chess.ChessGame;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.*;

public class FancyConsole extends ChessConsole {
    Terminal terminal;
    LineReader reader;
    boolean dummy;

    public FancyConsole(boolean dummy) {
        super();
        this.dummy = dummy;
    }

    private Collection<String> getBaseCommands() {
        if (prompting) {
            return commands == null ? List.of() : commands.keySet();
        } else {
            return Collections.emptyList();
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
            super.loop();
        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        }
        terminal = null;
        reader = null;
    }
}
