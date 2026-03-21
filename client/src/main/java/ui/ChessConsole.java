package ui;

import client.ServerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ChessConsole implements ChessTerminal {
    String prompt;
    Map<String, Command> commands;
    boolean running;
    boolean prompting;

    public ChessConsole() {
        running = false;
        prompt = "> ";
        prompting = false;
        commands = Map.of();
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void setCommands(List<Command> commands) {
        this.commands = new HashMap<>(commands.size());
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
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

    @Override
    public void loop() {
        running = true;
        while (running) {
            prompting = true;
            String line = prompt(prompt, false).strip().toLowerCase();
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
    }
}
