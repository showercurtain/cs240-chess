package ui;

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
    public void displayError(String message) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED+message+EscapeSequences.RESET_TEXT_COLOR);
        if (prompting) {
            System.out.print(prompt);
        }
    }

    @Override
    public void displayInfo(String message) {
        System.out.println(message);
        if (prompting) {
            System.out.print(prompt);
        }
    }
}
