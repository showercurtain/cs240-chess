package chess.moves;

public class Rook implements RayMovingPiece {
    private static final int[] RAYS = {0,2,4,6};

    @Override
    public int[] getRayDirections() {
        return RAYS;
    }
}
