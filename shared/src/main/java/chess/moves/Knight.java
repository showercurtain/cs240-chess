package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight implements MovingChessPiece {
    private static final ChessPosition.Offset[] OFFSETS = {
            new ChessPosition.Offset(1,2),
            new ChessPosition.Offset(2,1),
            new ChessPosition.Offset(1,-2),
            new ChessPosition.Offset(2,-1),
            new ChessPosition.Offset(-1,2),
            new ChessPosition.Offset(-2,1),
            new ChessPosition.Offset(-1,-2),
            new ChessPosition.Offset(-2,-1),
    };

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> out = new ArrayList<>();
        for (ChessPosition.Offset offset : OFFSETS) {
            ChessPosition newPos = myPosition.withOffset(offset);
            if (newPos.onBoard() && board.checkMove(newPos, color)) {
                out.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return out;
    }
}
