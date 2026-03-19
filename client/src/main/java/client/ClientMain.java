package client;

import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        ServerFacade server = new ServerFacade();
        Repl repl = new Repl(server);
        repl.start();
    }
}
