package model;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import model.codecs.ChessBoardCodec;
import model.codecs.ChessGameCodec;
import model.codecs.ChessPieceCodec;
import model.codecs.ChessPositionCodec;

public record GameData(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {
    public GameData copy() {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, new ChessGame(game));
    }
}
