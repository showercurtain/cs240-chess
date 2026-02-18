package model.codecs;

import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessPositionCodec
        implements JsonSerializer<ChessPosition>, JsonDeserializer<ChessPosition> {
    @Override
    public ChessPosition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return ChessPosition.fromString(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(ChessPosition chessPosition, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(chessPosition.toString());
    }
}
