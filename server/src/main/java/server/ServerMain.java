package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        int requestedPort;
        if (args.length == 0) requestedPort = 8080;
        else requestedPort = Integer.parseInt(args[0]);

        Server server = new Server();

        int port = server.run(requestedPort);
        System.out.printf("Server started on port %d", port);
    }
}
