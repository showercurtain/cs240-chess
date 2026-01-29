package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> getMoves(AbstractChessBoard board, ChessPosition position, ChessGame.TeamColor color);

    default Collection<ChessMove> captureMoves(AbstractChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = getMoves(board, myPosition, color);
        ArrayList<ChessMove> captureMoves = new ArrayList<>(8);

        for (ChessMove move : allMoves) {
            if (board.getPiece(move.endPosition()) != null) {
                captureMoves.add(move);
            }
        }

        return captureMoves;
    }

    default boolean validateMove(AbstractChessBoard board, ChessGame.TeamColor color, ChessMove move) {
        Collection<ChessMove> moves = getMoves(board, move.startPosition(), color);
        return moves.contains(move);
    }
}
