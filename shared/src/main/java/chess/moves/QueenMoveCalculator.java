package chess.moves;

import chess.ChessPosition;

public class QueenMoveCalculator implements RayMoveCalculator {
    private static final ChessPosition.Offset[] RAYS = {
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
    public ChessPosition.Offset[] getRays() {
        return RAYS;
    }
}
