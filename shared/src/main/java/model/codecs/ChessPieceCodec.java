package model.codecs;

import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPieceCodec
        implements JsonSerializer<ChessPiece>, JsonDeserializer<ChessPiece> {
    @Override
    public ChessPiece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ChessPiece.fromString(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(ChessPiece chessPiece, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(chessPiece.toString());
    }
}
