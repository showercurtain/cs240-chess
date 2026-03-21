package ui;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Scanner;

public class SimpleConsole extends ChessConsole {
    Scanner scanner;

    public SimpleConsole() {
        super();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String prompt(String prompt, boolean hidden) {
        System.out.print(prompt);
        return scanner.nextLine();
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
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED+message+EscapeSequences.RESET_TEXT_COLOR);
    }

    @Override
    public void displayInfo(String message) {
        System.out.println(message);
    }
}
