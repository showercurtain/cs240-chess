package ui;

import chess.ChessGame;
import chess.ChessPosition;
import model.GameData;

import java.util.List;

public interface ChessTerminal {
    String prompt(String prompt, boolean hidden);
    void setPrompt(String prompt);
    String genBoard(ChessGame game, ChessGame.TeamColor side, ChessPosition highlight);
    void showGame(GameData game, ChessGame.TeamColor side);
    void displayError(String message);
    void displayInfo(String message);
    void displayHelp();
    void setCommands(List<Command> commands);
    void loop();

    default void showBoard(ChessGame game, ChessGame.TeamColor side, ChessPosition highlight) {
        displayInfo(genBoard(game, side, highlight));
    }
}
