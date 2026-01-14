package chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int col) {
    public record Offset(int row_offset, int col_offset) {
        public static final Offset UP = new Offset(1,0);
        public static final Offset DOWN = new Offset(-1,0);
        public static final Offset LEFT = new Offset(0,-1);
        public static final Offset RIGHT = new Offset(0,1);
        /**
         * Directions:
         * 7 0 1
         * 6 * 2
         * 5 4 3
         * @param direction direction to offset
         * @return Offset towards specified direction
         */
        public static Offset offsetTo(int direction) {
            int col_offset = direction % 4 == 0 ? 0 : 1 - (direction / 4) * 2;
            direction = (direction + 2) % 8;
            int row_offset = direction % 4 == 0 ? 0 : 1 - (direction / 4) * 2;

            return new Offset(row_offset, col_offset);
        }

        public Offset addOffset(Offset offset) {
            return new Offset(row_offset + offset.row_offset, col_offset + offset.col_offset);
        }
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    /**
     * @return whether the ChessPosition is a valid position on the board
     */
    public boolean onBoard() {
        return row >= 0 && col >= 0 && row < 8 && col < 8;
    }

    /**
     * @param offset change in position
     * @return A new chess position with the specified offset
     */
    public ChessPosition withOffset(Offset offset) {
        return new ChessPosition(row + offset.row_offset(), col + offset.col_offset());
    }

    /**

     * Even numbers are rook rays, odd numbers are bishop rays
     * @param direction which direction the ray should travel
     * @return an ordered list of ChessPositions that represent a ray away from the object
     */
    public List<ChessPosition> getRay(int direction) {
        Offset offset = Offset.offsetTo(direction);

        ArrayList<ChessPosition> output = new ArrayList<>();
        for (
                ChessPosition current = withOffset(offset);
                current.onBoard();
                current = current.withOffset(offset)
        ) {
            output.add(current);
        }

        return output;
    }
}
