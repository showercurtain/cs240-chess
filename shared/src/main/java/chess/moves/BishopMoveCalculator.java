package chess.moves;

import chess.ChessPosition;

public class BishopMoveCalculator implements RayMoveCalculator {
    private static final ChessPosition.Offset[] RAYS = {
            ChessPosition.Offset.UPRIGHT,
            ChessPosition.Offset.UPLEFT,
            ChessPosition.Offset.DOWNLEFT,
            ChessPosition.Offset.DOWNRIGHT,
    };

    @Override
    public ChessPosition.Offset[] getRays() {
        return RAYS;
    }
}
