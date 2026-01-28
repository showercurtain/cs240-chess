package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color);
}
