package client;

import chess.*;
import ui.Repl;

import java.net.URI;
import java.net.URISyntaxException;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        try {
            ServerFacade server = new ServerFacade(new URI(args[0]));
            Repl repl = new Repl(server);
            repl.start();
        } catch (URISyntaxException e) {
            System.out.println("Invalid URI: " + e.getMessage());
        }

    }
}
