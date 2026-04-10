package model.codecs;

import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.lang.reflect.RecordComponent;

/*
I will admit that this code is from the internet. I do know what it's doing though, don't worry
 */
public class NullCheckTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (!rawType.isRecord()
                || rawType == GameData.class
                || rawType == UserGameCommand.class
                || rawType == ServerMessage.class
                || rawType == ChessMove.class) {
            return null;
        }

        TypeAdapter<T> delegate = gson.getDelegateAdapter(this, typeToken);
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter jsonWriter, T value) throws IOException {
                delegate.write(jsonWriter, value);
            }

            @Override
            public T read(JsonReader jsonReader) throws IOException {
                T out = delegate.read(jsonReader);
                if (out == null) return null;
                for (RecordComponent field : rawType.getRecordComponents()) {
                    try {
                        Object value = field.getAccessor().invoke(out);
                        if (value == null) {
                            throw new JsonParseException(
                                    "Missing field " + field.getName() + " in object " + rawType.getSimpleName());
                        }
                    } catch (ReflectiveOperationException e) {
                        throw new JsonParseException(e);
                    }
                }
                return out;
            }
        };
    }
}
