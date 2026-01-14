package chess.moves;

import chess.*;

import java.util.Collection;

public interface MovingChessPiece {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color);
}
