package chess.moves;

import chess.*;

public class KingMoveCalculator implements SetMoveCalculator {
    private static final ChessPosition.Offset[] MOVES = {
            ChessPosition.Offset.UP,
            ChessPosition.Offset.UPRIGHT,
            ChessPosition.Offset.RIGHT,
            ChessPosition.Offset.DOWNRIGHT,
            ChessPosition.Offset.DOWN,
            ChessPosition.Offset.DOWNLEFT,
            ChessPosition.Offset.LEFT,
            ChessPosition.Offset.UPLEFT,
    };

    @Override
    public ChessPosition.Offset[] getAllMoves() {
        return MOVES;
    }
}
