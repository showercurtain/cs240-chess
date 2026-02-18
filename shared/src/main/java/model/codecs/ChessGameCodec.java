package model.codecs;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessGameCodec
        implements JsonSerializer<ChessGame>, JsonDeserializer<ChessGame> {

    @Override
    public JsonElement serialize(ChessGame chessGame, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject out = new JsonObject();

        ChessBoardCodec boardSerializer = new ChessBoardCodec();

        out.add("board", boardSerializer.serialize(chessGame.getBoard(), ChessBoard.class, jsonSerializationContext));
        out.add("turn", jsonSerializationContext.serialize(chessGame.getTeamTurn()));

        return out;
    }

    @Override
    public ChessGame deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject in = jsonElement.getAsJsonObject();

        ChessBoardCodec boardDeserializer = new ChessBoardCodec();

        ChessBoard board = boardDeserializer.deserialize(in.get("board"), ChessBoard.class, jsonDeserializationContext);
        ChessGame.TeamColor turn = jsonDeserializationContext.deserialize(in.get("turn"), ChessGame.TeamColor.class);

        return new ChessGame(board, turn);
    }
}
