package client;

import chess.*;
import ui.Repl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");
        try {
            URI uri = args.length == 0 ? new URI("http://localhost:8080") : new URI(args[0]);
            ServerFacade server = new ServerFacade(uri);
            Repl repl = new Repl(server);
            repl.start();
        } catch (URISyntaxException e) {
            System.out.println("Invalid URI: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        }
    }
}
