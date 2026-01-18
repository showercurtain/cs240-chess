package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface MovingChessPiece {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color);

    default Collection<ChessMove> captureMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = pieceMoves(board, myPosition, color);
        ArrayList<ChessMove> captureMoves = new ArrayList<>(8);

        for (ChessMove move : allMoves) {
            if (board.getPiece(move.endPosition()) != null) {
                captureMoves.add(move);
            }
        }

        return captureMoves;
    }

    default boolean validateMove(ChessBoard board, ChessGame.TeamColor color, ChessMove move) {
        Collection<ChessMove> moves = pieceMoves(board, move.startPosition(), color);
        return moves.contains(move);
    }
}
