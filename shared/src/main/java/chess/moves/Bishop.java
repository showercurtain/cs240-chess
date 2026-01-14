package chess.moves;

public class Bishop implements RayMovingPiece {
    private static final int[] RAYS = {1,3,5,7};

    @Override
    public int[] getRayDirections() {
        return RAYS;
    }
}
