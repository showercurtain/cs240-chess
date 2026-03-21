package ui;

import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import java.io.IOException;
import java.util.*;

public class FancyConsole extends ChessConsole {
    Terminal terminal;
    LineReader reader;
    boolean dummy;
    final Highlighter highlighter = new Highlighter() {
        @Override
        public AttributedString highlight(LineReader lineReader, String buffer) {
            if (!prompting) {
                return new AttributedString(buffer);
            }
            for (Command command : commands.values()) {
                if (buffer.startsWith(command.getName())) {
                    AttributedStringBuilder builder = new AttributedStringBuilder();
                    builder.styled(
                            AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE),
                            command.getName());
                    builder.append(buffer.substring(command.getName().length()));
                    return builder.toAttributedString();
                }
            }
            return new AttributedString(buffer);
        }
    };

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
                    .highlighter(highlighter)
                    .build();
            super.loop();
        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        }
        terminal = null;
        reader = null;
    }
}
