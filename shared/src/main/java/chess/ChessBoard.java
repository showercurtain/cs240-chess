package chess;

import java.util.Arrays;
import java.util.EnumSet;

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

    public enum CastleState {
        B_KING, // Black castle on king's side O-O
        B_QUEEN, // Black castle on queen's side O-O-O
        W_KING, // White castle on king's side O-O
        W_QUEEN, // White castle on queen's side O-O-O
    }

    final ChessPiece[] pieces;

    ChessPosition enPassant; // Stores the location that a pawn skips over on a move
    EnumSet<CastleState> castleStates; // Stores which castle moves are still possible

    public ChessBoard() {
        pieces = new ChessPiece[64];
        enPassant = null;
        castleStates = EnumSet.noneOf(CastleState.class);
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
        Arrays.fill(pieces, null);

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

        enPassant = null;
        castleStates = EnumSet.allOf(CastleState.class);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (getClass() != obj.getClass()) return false;
        ChessBoard other = (ChessBoard) obj;
        for (int i=0; i < 64; i++) {
            if (pieces[i] == null) {
                if (other.pieces[i] != null) return false;
            } else if (!pieces[i].equals(other.pieces[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pieces);
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
}
