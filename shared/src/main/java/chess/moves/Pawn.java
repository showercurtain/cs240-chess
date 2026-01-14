package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn implements MovingChessPiece {
    public Pawn() { }

    private static ChessPosition.Offset forwardPosition(ChessGame.TeamColor color) {
        // The pawn is the only chess piece that cares about the direction it's facing
        // This direction is based on the directions used in ChessPosition.Offset
        return color == ChessGame.TeamColor.BLACK ? ChessPosition.Offset.DOWN : ChessPosition.Offset.UP;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition.Offset forwardOffset = forwardPosition(color);
        ChessPosition.Offset leftOffset = forwardOffset.addOffset(ChessPosition.Offset.LEFT);
        ChessPosition.Offset rightOffset = forwardOffset.addOffset(ChessPosition.Offset.RIGHT);
        ChessPosition.Offset forwardTwoOffset = forwardOffset.addOffset(forwardOffset);

        ChessPosition forward = myPosition.withOffset(forwardOffset);
        ChessPosition left = myPosition.withOffset(leftOffset);
        ChessPosition right = myPosition.withOffset(rightOffset);
        ChessPosition forwardTwo = myPosition.withOffset(forwardTwoOffset);

        // Normal forward movement
        if (forward.onBoard() && board.getPiece(forward) == null) {
            moves.add(new ChessMove(myPosition, forward, null));
            // Test cases require that the pawn can move one extra space
            // when in the starting rank, NOT on it's first move
            if (forwardTwo.onBoard() && (myPosition.getRow() == 2 || myPosition.getRow() == 7) && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(myPosition, forwardTwo, null));
            }
        }

        // Capturing
        if (left.onBoard() && board.checkCapture(left, color)) {
            moves.add(new ChessMove(myPosition, left, null));
        }

        if (right.onBoard() && board.checkCapture(right, color)) {
            moves.add(new ChessMove(myPosition, right, null));
        }

        ArrayList<ChessMove> promoteMoves = new ArrayList<>();

        for (ChessMove move : moves) {
            if (move.endPosition().row() == 1 || move.endPosition().row() == 8) {
                promoteMoves.add(move.withPromotion(ChessPiece.PieceType.BISHOP));
                promoteMoves.add(move.withPromotion(ChessPiece.PieceType.ROOK));
                promoteMoves.add(move.withPromotion(ChessPiece.PieceType.KNIGHT));
                promoteMoves.add(move.withPromotion(ChessPiece.PieceType.QUEEN));
            } else {
                promoteMoves.add(move);
            }
        }

        return promoteMoves;
    }
}
