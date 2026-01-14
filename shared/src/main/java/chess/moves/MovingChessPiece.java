package chess.moves;

import chess.*;

import java.util.Collection;

public interface MovingChessPiece {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color);

    default boolean validateMove(ChessBoard board, ChessGame.TeamColor color, ChessMove move) {
        Collection<ChessMove> moves = pieceMoves(board, move.startPosition(), color);
        return moves.contains(move);
    }
}
