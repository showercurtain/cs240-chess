package chess.moves;

import chess.ChessPosition;

public class KnightMoveCalculator implements SetMoveCalculator {
    private static final ChessPosition.Offset[] MOVES = {
            new ChessPosition.Offset(1,2),
            new ChessPosition.Offset(2,1),
            new ChessPosition.Offset(1,-2),
            new ChessPosition.Offset(-2,1),
            new ChessPosition.Offset(-1,-2),
            new ChessPosition.Offset(-2,-1),
            new ChessPosition.Offset(-1,2),
            new ChessPosition.Offset(2,-1),
    };

    @Override
    public ChessPosition.Offset[] getAllMoves() {
        return MOVES;
    }
}
