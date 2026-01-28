package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface SetMoveCalculator extends MoveCalculator {
    ChessPosition.Offset[] getAllMoves();

    default Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> out = new ArrayList<>();

        for (ChessPosition.Offset move : getAllMoves()) {
            ChessPosition pos = position.withOffset(move);

            if (pos.onBoard()) {
                ChessPiece piece = board.getPiece(pos);
                if (piece == null || piece.pieceColor() != color) {
                    out.add(new ChessMove(position, pos, null));
                }
            }
        }

        return out;
    }
}
