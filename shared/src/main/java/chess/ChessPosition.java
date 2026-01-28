package chess;

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
        public static final Offset UPLEFT = new Offset(1,-1);
        public static final Offset DOWNRIGHT = new Offset(-1,1);
        public static final Offset DOWNLEFT = new Offset(-1,-1);
        public static final Offset UPRIGHT = new Offset(1,1);
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
        return row > 0 && col > 0 && row <= 8 && col <= 8;
    }

    /**
     * @param offset change in position
     * @return A new chess position with the specified offset
     */
    public ChessPosition withOffset(Offset offset) {
        return new ChessPosition(row + offset.row_offset(), col + offset.col_offset());
    }
}
