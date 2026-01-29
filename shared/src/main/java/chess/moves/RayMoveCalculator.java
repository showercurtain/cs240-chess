package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface RayMoveCalculator extends MoveCalculator {
    ChessPosition.Offset[] getRays();

    @Override
    default Collection<ChessMove> getMoves(AbstractChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ArrayList<ChessMove> out = new ArrayList<>();

        for (ChessPosition.Offset ray : getRays()) {
            ChessPosition pos = position;

            while (true) {
                pos = pos.withOffset(ray);
                if (pos.onBoard()) {
                    ChessPiece piece = board.getPiece(pos);
                    if (piece != null) {
                        if (piece.pieceColor() != color) {
                            out.add(new ChessMove(position, pos, null));
                        }
                        break;
                    }
                    out.add(new ChessMove(position, pos, null));
                } else {
                    break;
                }
            }
        }

        return out;
    }
}
