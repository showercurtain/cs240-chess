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
        if (prompting) {
            System.out.print("\r");
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED+message+EscapeSequences.RESET_TEXT_COLOR);
        if (prompting) {
            System.out.print(prompt);
        }
        System.out.flush();
    }

    @Override
    public void displayInfo(String message) {
        if (prompting) {
            System.out.print("\r");
        }
        System.out.println(message);
        if (prompting) {
            System.out.print(prompt);
        }
        System.out.flush();
    }
}
