package ui;

import client.ServerException;

import java.util.*;
import java.util.function.Function;

public interface Command {
    String getName();
    String getDescription();
    List<String> getRequiredParameters();
    List<String> getExtraParameters();
    Collection<String> getValidValues(Map<String, String> args, String param);
    boolean execute(Map<String, String> args) throws ServerException;

    interface BasicCommand {
        void run() throws ServerException;
    }

    interface CommandWithArgs {
        void run(Map<String, String> args) throws ServerException;
    }

    interface FullCommand {
        boolean run(Map<String, String> args) throws ServerException;
    }

    interface ExitCommand {
        boolean run() throws ServerException;
    }

    static Command makeCommand(FullCommand command, String name, String description,
                               List<String> requiredArguments, List<String> extraArguments,
                               Map<String, Function<Map<String, String>,Collection<String>>> validValues) {
        return new Command() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public List<String> getRequiredParameters() {
                return requiredArguments;
            }

            @Override
            public List<String> getExtraParameters() {
                return extraArguments;
            }

            @Override
            public Collection<String> getValidValues(Map<String, String> args, String arg) {
                Function<Map<String, String>,Collection<String>> function = validValues.get(arg);
                if (function == null) {
                    return Collections.emptyList();
                }
                return function.apply(args);
            }

            @Override
            public boolean execute(Map<String, String> args) throws ServerException {
                return command.run(args);
            }
        };
    }

    static Command makeCommand(BasicCommand command, String name, String description) {
        return makeCommand((Map<String, String> args) -> {
                command.run();
                return false;
            },
                name, description, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
    }

    static Command makeCommand(ExitCommand command, String name, String description) {
        return makeCommand((Map<String, String> args) -> command.run(),
                name, description, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
    }

    static Command makeCommand(CommandWithArgs command, String name, String description,
                               List<String> requiredArguments, List<String> extraArguments) {
        return makeCommand((Map<String, String> args) -> {
            command.run(args);
            return false;
        }, name, description, requiredArguments, extraArguments, Collections.emptyMap());
    }

    static Command makeCommand(FullCommand command, String name, String description,
                               List<String> requiredArguments, List<String> extraArguments) {
        return makeCommand(command, name, description, requiredArguments, extraArguments, Collections.emptyMap());
    }

    static Command makeCommand(CommandWithArgs command, String name, String description,
                               List<String> requiredArguments, List<String> extraArguments,
                               Map<String, Function<Map<String, String>,Collection<String>>> validValues) {
        return makeCommand((Map<String, String> args) -> {
            command.run(args);
            return false;
        }, name, description, requiredArguments, extraArguments, validValues);
    }
}
