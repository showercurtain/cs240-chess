package ui;

import chess.*;
import client.ServerException;
import model.GameData;

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
        int[] lengths = new int[commands.size()];
        int maxLength = 0;
        int i = 0;
        for (Command command : commands.values()) {
            int length = command.getName().length();
            for (String arg : command.getRequiredArguments()) {
                length += 3 + arg.length();
            }
            for (String arg : command.getExtraArguments()) {
                length += 3 + arg.length();
            }
            if (length > maxLength) {
                maxLength = length;
            }
            lengths[i] = length;
            i += 1;
        }

        i = 0;
        for (Command command : commands.values()) {
            StringBuilder line = new StringBuilder();
            line.append("  ")
                    .append(EscapeSequences.SET_TEXT_COLOR_BLUE).append(command.getName())
                    .append(EscapeSequences.RESET_TEXT_COLOR);
            for (String arg : command.getRequiredArguments()) {
                line.append(" [").append(arg).append("]");
            }
            for (String arg : command.getExtraArguments()) {
                line.append(" <").append(arg).append(">");
            }
            line.append(String.format("%" + (maxLength - lengths[i] + 1) + "s",""));
            line.append(": ").append(command.getDescription());
            displayInfo(line.toString());
            i += 1;
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
            String line = prompt(prompt, false).strip();
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

    private static String getPieceString(boolean drawWhite, ChessPiece piece) {
        boolean outline = drawWhite != (piece.getTeamColor().equals(ChessGame.TeamColor.BLACK));
        return outline ? switch (piece.getPieceType()) {
            case KING -> EscapeSequences.WHITE_KING;
            case QUEEN -> EscapeSequences.WHITE_QUEEN;
            case BISHOP -> EscapeSequences.WHITE_BISHOP;
            case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
            case ROOK -> EscapeSequences.WHITE_ROOK;
            case PAWN -> EscapeSequences.WHITE_PAWN;
            case DUMMY -> EscapeSequences.EMPTY;
        } : switch (piece.getPieceType()) {
            case KING -> EscapeSequences.BLACK_KING;
            case QUEEN -> EscapeSequences.BLACK_QUEEN;
            case BISHOP -> EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            case ROOK -> EscapeSequences.BLACK_ROOK;
            case PAWN -> EscapeSequences.BLACK_PAWN;
            case DUMMY -> EscapeSequences.EMPTY;
        };
    }

    private void buildChessRow(StringBuilder builder, int row, boolean reverse, AbstractChessBoard board) {
        builder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        builder.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        builder.append(" ").append(row).append(" ");
        boolean drawWhite = (row%2==1) != reverse;
        for (int col=1; col<=8; col++) {
            if (drawWhite) {
                builder.append(EscapeSequences.SET_BG_COLOR_WHITE);
                builder.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            } else {
                builder.append(EscapeSequences.SET_BG_COLOR_BLACK);
                builder.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
            }
            ChessPiece piece = board.getPiece(new ChessPosition(row, col));
            if (piece == null) {
                builder.append(EscapeSequences.EMPTY);
            } else {
                String square = getPieceString(drawWhite, piece);
                builder.append(square);
            }
            drawWhite = !drawWhite;
        }
        builder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        builder.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        builder.append(" ").append(row).append(" ");
        builder.append(EscapeSequences.RESET_BG_COLOR);
        builder.append(EscapeSequences.RESET_TEXT_COLOR);
        builder.append("\n");
    }

    private void buildEndRow(StringBuilder builder, boolean reverse) {
        String letters = "abcdefgh";
        builder.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        builder.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        builder.append("   ");
        if (reverse) {
            for (int i=7; i>=0; i--) {
                builder.append(" ").append(letters.charAt(i)).append(" ");
            }
        } else {
            for (int i=0; i<8; i++) {
                builder.append(" ").append(letters.charAt(i)).append(" ");
            }
        }
        builder.append("   ");
        builder.append(EscapeSequences.RESET_BG_COLOR);
        builder.append(EscapeSequences.RESET_TEXT_COLOR);
        builder.append("\n");
    }

    private void buildBoard(StringBuilder builder, ChessBoard board, ChessGame.TeamColor side) {
        boolean reverse = side.equals(ChessGame.TeamColor.BLACK);
        buildEndRow(builder, reverse);
        if (reverse) {
            for (int i=1; i<=8; i++) {
                buildChessRow(builder, i, true, board);
            }
        } else {
            for (int i=8; i>=1; i--) {
                buildChessRow(builder, i, false, board);
            }
        }
        buildEndRow(builder, reverse);
    }

    @Override
    public void showBoard(ChessBoard board, ChessGame.TeamColor side) {
        StringBuilder builder = new StringBuilder();
        buildBoard(builder, board, side);
        displayInfo(builder.toString());
    }

    @Override
    public void showGame(GameData game, ChessGame.TeamColor side) {
        StringBuilder builder = new StringBuilder();
        int padding = (30 + game.gameName().length()) / 2;
        builder.append(String.format("%" + padding + "s\n", game.gameName()));
        buildBoard(builder, game.game().getBoard(), side);
        builder.append("White: ");
        if (game.whiteUsername() == null) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_BLUE).append("Unclaimed").append(EscapeSequences.RESET_TEXT_COLOR).append("\n");
        } else {
            builder.append(game.whiteUsername()).append("\n");
        }
        builder.append("Black: ");
        if (game.blackUsername() == null) {
            builder.append(EscapeSequences.SET_TEXT_COLOR_BLUE).append("Unclaimed").append(EscapeSequences.RESET_TEXT_COLOR).append("\n");
        } else {
            builder.append(game.blackUsername()).append("\n");
        }
        displayInfo(builder.toString());
    }
}
