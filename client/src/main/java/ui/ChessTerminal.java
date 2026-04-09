package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface ChessTerminal {
    String prompt(String prompt, boolean hidden);
    void setPrompt(String prompt);
    String genBoard(ChessBoard board, ChessGame.TeamColor side);
    void showGame(GameData game, ChessGame.TeamColor side);
    void displayError(String message);
    void displayInfo(String message);
    void displayHelp();
    void setCommands(List<Command> commands);
    void loop();

    default void showBoard(ChessBoard board, ChessGame.TeamColor side) {
        displayInfo(genBoard(board, side));
    }
}
