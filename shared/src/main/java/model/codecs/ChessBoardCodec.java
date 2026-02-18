package model.codecs;

import chess.*;
import com.google.gson.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Map;

public class ChessBoardCodec
        implements JsonSerializer<ChessBoard>, JsonDeserializer<ChessBoard> {

    @Override
    public ChessBoard deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject in = jsonElement.getAsJsonObject();
        ChessBoard board = new ChessBoard();
        board.setEnPassant(jsonDeserializationContext.deserialize(in.get("enPassant"), ChessPosition.class));

        EnumSet<AbstractChessBoard.CastleState> castleStates = EnumSet.noneOf(AbstractChessBoard.CastleState.class);
        for (JsonElement state : in.getAsJsonArray("castleState").asList()) {
            castleStates.add(jsonDeserializationContext.deserialize(state, AbstractChessBoard.CastleState.class));
        }
        board.setCastleStates(castleStates);

        JsonObject pieces = in.getAsJsonObject("pieces");
        for (Map.Entry<String, JsonElement> entry : pieces.entrySet()) {
            ChessPosition pos = ChessPosition.fromString(entry.getKey());
            board.addPiece(pos,
                    ChessPiece.fromString(entry.getValue().getAsString()));
        }

        return board;
    }

    @Override
    public JsonElement serialize(ChessBoard chessBoard, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject out = new JsonObject();
        out.add("enPassant", jsonSerializationContext.serialize(chessBoard.getEnPassant()));
        out.add("castleState", jsonSerializationContext.serialize(chessBoard.getCastleStates()));
        out.add("pieces", jsonSerializationContext.serialize(chessBoard.getPieces()));
        return out;
    }
}
