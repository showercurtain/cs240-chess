package chess.moves;

import chess.ChessPosition;

public class RookMoveCalculator implements RayMoveCalculator {
    private static final ChessPosition.Offset[] RAYS = {
            ChessPosition.Offset.UP,
            ChessPosition.Offset.RIGHT,
            ChessPosition.Offset.DOWN,
            ChessPosition.Offset.LEFT,
    };

    @Override
    public ChessPosition.Offset[] getRays() {
        return RAYS;
    }
}
