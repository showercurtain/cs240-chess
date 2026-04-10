package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public record ServerMessage(
        ServerMessageType serverMessageType,
        ChessGame game,
        String errorMessage,
        String message
) {


    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

//    public ServerMessage(ServerMessageType type) {
//        this(type, Optional.empty(), Optional.empty(), Optional.empty());
//    }

    public ServerMessage(ChessGame game) {
        this(
                ServerMessageType.LOAD_GAME,
                game,
                null,
                null
        );
    }

    public ServerMessage(String message, boolean error) {
        this(
                error ? ServerMessageType.ERROR : ServerMessageType.NOTIFICATION,
                null,
                error ? message : null,
                error ? null : message
        );
    }

    public ServerMessage(String message) {
        this(message, false);
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
