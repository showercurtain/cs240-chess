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
    boolean dummy;
    final Highlighter highlighter = new Highlighter() {
        @Override
        public AttributedString highlight(LineReader lineReader, String buffer) {
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
            Iterator<String> args = Stream.concat(command.getRequiredArguments().stream(), command.getExtraArguments().stream()).iterator();
            if (!args.hasNext()) {
                builder.append(buffer.substring(end));
                return builder.toAttributedString();
            }
            start = -1;
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
                    String argName = args.next();
                    if (command.getValidValues(argName).contains(arg)) {
                        builder.styled(
                                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW),
                                buffer.substring(oldEnd, end)
                        );
                    } else {
                        builder.append(buffer.substring(oldEnd, end));
                    }
                    oldEnd = end;
                    if (!args.hasNext()) {
                        break;
                    }
                } else if (c != ' ' && start == -1) {
                    start = i;
                }
            }
            builder.append(buffer.substring(end));

            return builder.toAttributedString();
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
