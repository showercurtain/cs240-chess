package model;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.codecs.*;

import java.util.Optional;

public class GsonUtil {
    public static Gson buildGson() {
        // Temporary fix to pass the obnoxious tests
        return new GsonBuilder()
                .registerTypeAdapterFactory(new NullCheckTypeAdapterFactory())
                .create();
    }
}
