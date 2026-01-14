package chess.moves;

public class Queen implements RayMovingPiece {
    private static final int[] RAYS = {0,1,2,3,4,5,6,7};

    @Override
    public int[] getRayDirections() {
        return RAYS;
    }
}
