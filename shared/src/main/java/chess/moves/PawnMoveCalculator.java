package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoveCalculator implements MoveCalculator {
    private static final ChessPiece.PieceType[] PROMOTION_PIECES = {
            ChessPiece.PieceType.BISHOP,
            ChessPiece.PieceType.ROOK,
            ChessPiece.PieceType.QUEEN,
            ChessPiece.PieceType.KNIGHT,
    };

    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        ChessPosition.Offset direction = switch (color) {
            case WHITE -> ChessPosition.Offset.UP;
            case BLACK -> ChessPosition.Offset.DOWN;
        };
        ArrayList<ChessPosition> endPositions = new ArrayList<>(4);

        ChessPosition move = position.withOffset(direction);
        if (move.onBoard() && board.getPiece(move) == null) {
            endPositions.add(move);
            if (position.row() == 2 || position.row() == 7) {
                ChessPosition move2 = move.withOffset(direction);
                if (move2.onBoard() && board.getPiece(move2) == null) {
                    endPositions.add(move2);
                }
            }
        }

        ChessPosition cap1 = move.withOffset(ChessPosition.Offset.LEFT);
        if (cap1.onBoard()) {
            ChessPiece piece = board.getPiece(cap1);
            if (piece != null && piece.pieceColor() != color) {
                endPositions.add(cap1);
            }
        }

        ChessPosition cap2 = move.withOffset(ChessPosition.Offset.RIGHT);
        if (cap2.onBoard()) {
            ChessPiece piece = board.getPiece(cap2);
            if (piece != null && piece.pieceColor() != color) {
                endPositions.add(cap2);
            }
        }

        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessPosition end : endPositions) {
            if (end.row() == 1 || end.row() == 8) {
                for (ChessPiece.PieceType type : PROMOTION_PIECES) {
                    moves.add(new ChessMove(position, end, type));
                }
            } else {
                moves.add(new ChessMove(position, end, null));
            }
        }

        return moves;
    }
}
