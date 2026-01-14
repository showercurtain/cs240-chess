package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private static final ChessPiece W_PAWN = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
    private static final ChessPiece W_ROOK = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
    private static final ChessPiece W_KNIGHT = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
    private static final ChessPiece W_BISHOP = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
    private static final ChessPiece W_KING = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
    private static final ChessPiece W_QUEEN = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
    private static final ChessPiece B_PAWN = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
    private static final ChessPiece B_ROOK = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
    private static final ChessPiece B_KNIGHT = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
    private static final ChessPiece B_BISHOP = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
    private static final ChessPiece B_KING = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    private static final ChessPiece B_QUEEN = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

    ChessPiece[] pieces;

    public ChessBoard() {
        pieces = new ChessPiece[64];
        //resetBoard();
    }

    private static int indexOf(ChessPosition position) {
        return (position.col()-1) * 8 + (position.row()-1);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces[indexOf(position)] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[indexOf(position)];
    }

    public boolean checkCapture(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(position);
        return piece != null && piece.getTeamColor() != color;
    }

    public boolean checkMove(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(position);
        return piece == null || piece.getTeamColor() != color;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(i, 2), W_PAWN);
            addPiece(new ChessPosition(i, 7), B_PAWN);
        }

        addPiece(new ChessPosition(1, 1), W_ROOK);
        addPiece(new ChessPosition(1, 8), B_ROOK);

        addPiece(new ChessPosition(8, 1), W_ROOK);
        addPiece(new ChessPosition(8, 8), B_ROOK);

        addPiece(new ChessPosition(2, 1), W_KNIGHT);
        addPiece(new ChessPosition(2, 8), B_KNIGHT);

        addPiece(new ChessPosition(7, 1), W_KNIGHT);
        addPiece(new ChessPosition(7, 8), B_KNIGHT);

        addPiece(new ChessPosition(3, 1), W_BISHOP);
        addPiece(new ChessPosition(3, 8), B_BISHOP);

        addPiece(new ChessPosition(6, 1), W_BISHOP);
        addPiece(new ChessPosition(6, 8), B_BISHOP);

        addPiece(new ChessPosition(4, 1), W_KING);
        addPiece(new ChessPosition(4, 8), B_KING);

        addPiece(new ChessPosition(5, 1), W_KING);
        addPiece(new ChessPosition(5, 8), B_KING);
    }
}
