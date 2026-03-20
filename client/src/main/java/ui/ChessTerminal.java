package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.List;

public interface ChessTerminal {
    String prompt(String prompt, boolean hidden);
    void setPrompt(String prompt);
    void showBoard(ChessBoard board, ChessGame.TeamColor side);
    void showGame(ChessGame game, ChessGame.TeamColor side);
    void displayError(String message);
    void displayInfo(String message);
    void displayHelp();
    void setCommands(List<Command> commands);
    void loop();
}
