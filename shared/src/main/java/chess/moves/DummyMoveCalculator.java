package chess.moves;

import chess.AbstractChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

// Used only for en-passant calculations
public class DummyMoveCalculator implements MoveCalculator {

    @Override
    public Collection<ChessMove> getMoves(AbstractChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        return List.of();
    }

    @Override
    public Collection<ChessMove> captureMoves(AbstractChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        return List.of();
    }

    @Override
    public boolean validateMove(AbstractChessBoard board, ChessGame.TeamColor color, ChessMove move) {
        return false;
    }
}
