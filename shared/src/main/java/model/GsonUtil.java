package model;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.codecs.ChessBoardCodec;
import model.codecs.ChessGameCodec;
import model.codecs.ChessPieceCodec;
import model.codecs.ChessPositionCodec;

public class GsonUtil {
    public static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardCodec())
                .registerTypeAdapter(ChessGame.class, new ChessGameCodec())
                .registerTypeAdapter(ChessPiece.class, new ChessPieceCodec())
                .registerTypeAdapter(ChessPosition.class, new ChessPositionCodec())
                .create();
    }
}
