package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King implements MovingChessPiece {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> out = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            ChessPosition newPos = myPosition.withOffset(ChessPosition.Offset.offsetTo(i));
            if (newPos.onBoard() && board.checkMove(newPos, color)) {
                out.add(new ChessMove(myPosition, newPos, null));
            }
        }

        return out;
    }
}
