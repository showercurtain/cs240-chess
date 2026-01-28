package chess;

import chess.moves.*;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
    /**
     * The various different chess piece options
     */
    public enum PieceType implements MoveCalculator {
        KING(new KingMoveCalculator()),
        QUEEN(new QueenMoveCalculator()),
        BISHOP(new BishopMoveCalculator()),
        KNIGHT(new KnightMoveCalculator()),
        ROOK(new RookMoveCalculator()),
        PAWN(new PawnMoveCalculator());

        PieceType(MoveCalculator moves) {
            this.MOVE_CALCULATOR = moves;
        }

        private final MoveCalculator MOVE_CALCULATOR;

        @Override
        public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
            return MOVE_CALCULATOR.getMoves(board, position, color);
        }
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return type.getMoves(board, myPosition, pieceColor);
    }
}
