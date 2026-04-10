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
import java.util.stream.Stream;

public class FancyConsole extends ChessConsole {
    Terminal terminal;
    LineReader reader;
    final Highlighter highlighter = (lineReader, buffer) -> {
        if (!prompting || buffer.isEmpty()) {
            return new AttributedString(buffer);
        }
        int start = -1;
        int end = -1;
        for (int i = 0; i < buffer.length(); i++) {
            char c = buffer.charAt(i);
            if (c == ' ' && start != -1) {
                end = i;
                break;
            } else if (c != ' ' && start == -1) {
                start = i;
            }
        }
        if (end == -1) {
            end = buffer.length();
        }
        if (start == -1) {
            return new AttributedString(buffer);
        }
        String cmdString = buffer.substring(start, end);
        Command command = commands.get(cmdString);
        if (command == null) {
            return new AttributedString(buffer);
        }
        AttributedStringBuilder builder = new AttributedStringBuilder();
        builder.styled(
                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE),
                buffer.substring(0, end)
        );
        Iterator<String> parameters = Stream.concat(command.getRequiredParameters().stream(), command.getExtraParameters().stream()).iterator();
        if (!parameters.hasNext()) {
            builder.append(buffer.substring(end));
            return builder.toAttributedString();
        }
        start = -1;
        HashMap<String, String> args = new HashMap<>();
        boolean valid = true;
        int oldEnd = end;
        for (int i = end; i <= buffer.length(); i++) {
            char c;
            if (i == buffer.length()) {
                c = ' ';
            } else {
                c = buffer.charAt(i);
            }
            if (c == ' ' && start != -1) {
                end = i;
                String arg = buffer.substring(start, end);
                String paramName = parameters.next();
                if (valid && command.getValidValues(args, paramName).contains(arg)) {
                    builder.styled(
                            AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW),
                            buffer.substring(oldEnd, end)
                    );
                    args.put(paramName, arg);
                } else {
                    builder.append(buffer.substring(oldEnd, end));
                    valid = false;
                }
                oldEnd = end;
                if (!parameters.hasNext()) {
                    break;
                }
            } else if (c != ' ' && start == -1) {
                start = i;
            }
        }
        builder.append(buffer.substring(end));

        return builder.toAttributedString();
    };

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
        reader.printAbove(EscapeSequences.SET_TEXT_COLOR_RED+message+EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void displayInfo(String message) {
        reader.printAbove(message);
    }

    @Override
    public void loop() {
        TerminalBuilder builder = TerminalBuilder.builder()
                .system(true);
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
