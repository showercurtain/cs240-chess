package chess;

import java.util.*;

public abstract class AbstractChessBoard {
    public enum CastleState {
        B_KING, // Black castle on king's side O-O
        B_QUEEN, // Black castle on queen's side O-O-O
        W_KING, // White castle on king's side O-O
        W_QUEEN, // White castle on queen's side O-O-O
    }

    public static final ChessPiece W_PAWN = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
    public static final ChessPiece W_ROOK = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
    public static final ChessPiece W_KNIGHT = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
    public static final ChessPiece W_BISHOP = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
    public static final ChessPiece W_KING = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
    public static final ChessPiece W_QUEEN = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
    public static final ChessPiece B_PAWN = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
    public static final ChessPiece B_ROOK = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
    public static final ChessPiece B_KNIGHT = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
    public static final ChessPiece B_BISHOP = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
    public static final ChessPiece B_KING = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    public static final ChessPiece B_QUEEN = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);

    ChessPosition enPassant; // Stores the location that a pawn skips over on a move
    EnumSet<CastleState> castleStates; // Stores which castle moves are still possible

    public AbstractChessBoard() {
        enPassant = null;
        castleStates = EnumSet.noneOf(CastleState.class);
    }

    public ChessPosition getEnPassant() {
        return enPassant;
    }

    public EnumSet<CastleState> getCastleStates() {
        return castleStates;
    }

    public boolean mightCastle(CastleState move) {
        return castleStates.contains(move);
    }

    public void setEnPassant(ChessPosition enPassant) {
        this.enPassant = enPassant;
    }

    public void clearCastleState(CastleState state) {
        castleStates.remove(state);
    }

    /**
     * Checks whether a piece of a specified color can capture a piece at a location.
     * Note that this function doesn't check whether the move is valid for that piece, only
     * that it is a valid location for a piece to capture.
     * @param position The position to check
     * @param color The color of the moving piece
     * @return Whether there is a piece at that location that can be captured
     */
    public boolean checkCapture(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(position);
        return piece != null && piece.getTeamColor() != color;
    }

    /**
     * Checks whether a piece can move to a location, including capturing another piece
     * Note that this function doesn't check whether the move is valid for that piece, only
     * that it is a valid location for a piece to move to.
     * @param position The position to check
     * @param color The color of the moving piece
     * @return True if the piece can move to that location
     */
    public boolean checkMove(ChessPosition position, ChessGame.TeamColor color) {
        ChessPiece piece = getPiece(position);
        return piece == null || piece.getTeamColor() != color;
    }

    /**
     * Searches for the king of a specific color on the board
     * @param color The color of the king to be searched for
     * @return Position of the king on the board
     * @throws RuntimeException when the king is not found, which is not possible during normal gameplay
     */
    public ChessPosition findKing(ChessGame.TeamColor color) {
        ChessPiece king = color == ChessGame.TeamColor.WHITE ? W_KING : B_KING;
        for (Map.Entry<ChessPosition, ChessPiece> entry : getPieces().entrySet()) {
            if (entry.getValue().equals(king)) return entry.getKey();
        }
        throw new RuntimeException("Invalid state: No king found");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearBoard();

        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), W_PAWN);
            addPiece(new ChessPosition(7, i), B_PAWN);
        }

        addPiece(new ChessPosition(1, 1), W_ROOK);
        addPiece(new ChessPosition(8, 1), B_ROOK);

        addPiece(new ChessPosition(1, 8), W_ROOK);
        addPiece(new ChessPosition(8, 8), B_ROOK);

        addPiece(new ChessPosition(1, 2), W_KNIGHT);
        addPiece(new ChessPosition(8, 2), B_KNIGHT);

        addPiece(new ChessPosition(1, 7), W_KNIGHT);
        addPiece(new ChessPosition(8, 7), B_KNIGHT);

        addPiece(new ChessPosition(1, 3), W_BISHOP);
        addPiece(new ChessPosition(8, 3), B_BISHOP);

        addPiece(new ChessPosition(1, 6), W_BISHOP);
        addPiece(new ChessPosition(8, 6), B_BISHOP);

        addPiece(new ChessPosition(1, 4), W_QUEEN);
        addPiece(new ChessPosition(8, 4), B_QUEEN);

        addPiece(new ChessPosition(1, 5), W_KING);
        addPiece(new ChessPosition(8, 5), B_KING);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row=8; row >= 1; row--) {
            builder.append('|');
            for (int col=1; col <= 8; col++) {
                ChessPiece piece = getPiece(new ChessPosition(row, col));
                if (piece == null) {
                    builder.append(" |");
                    continue;
                }
                char rep = ' ';
                boolean upper = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
                switch (piece.getPieceType()) {
                    case PAWN -> rep = 'p';
                    case ROOK -> rep = 'r';
                    case KNIGHT -> rep = 'n';
                    case BISHOP -> rep = 'b';
                    case QUEEN -> rep = 'q';
                    case KING -> rep = 'k';
                }
                if (upper) {
                    builder.append(Character.toUpperCase(rep));
                } else {
                    builder.append(rep);
                }
                builder.append('|');
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof AbstractChessBoard other)) return false;
        Map<ChessPosition, ChessPiece> myPieces = getPieces();
        Map<ChessPosition, ChessPiece> otherPieces = other.getPieces();
        return myPieces.equals(otherPieces);
    }

    @Override
    public int hashCode() {
        return getPieces().hashCode();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public abstract void addPiece(ChessPosition position, ChessPiece piece);

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public abstract ChessPiece getPiece(ChessPosition position);

    public void movePiece(ChessMove move) {
        addPiece(move.endPosition(), getPiece(move.startPosition()));
        addPiece(move.startPosition(), null);
    }

    /**
     * Clears the board of all pieces
     */
    public abstract void clearBoard();

    public abstract Map<ChessPosition, ChessPiece> getPieces();

    public boolean isInDanger(ChessPosition position) {
        ChessGame.TeamColor team = getPiece(position).getTeamColor();

        // Loop through every kind of piece
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            // If that piece can capture itself from where the king is, then the king is in check
            // This should always work when black and white pieces move the same (which is always, go figure)
            for (ChessMove move : type.captureMoves(this, position, team)) {
                if (getPiece(move.endPosition()).type().equals(type)) return true;
            }
        }

        return false;
    }

    public Collection<ChessMove> getAllMoves(ChessGame.TeamColor team) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (Map.Entry<ChessPosition, ChessPiece> entry : getPieces().entrySet()) {
            if (!entry.getValue().getTeamColor().equals(team)) continue;
            moves.addAll(entry.getValue().type().getMoves(this, entry.getKey(), team));
        }

        return moves;
    }
}
