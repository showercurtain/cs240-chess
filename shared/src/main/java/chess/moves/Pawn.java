package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn implements MovingChessPiece {
    private boolean moved;
    private boolean skipped;

    public Pawn() {
        moved = false;
        skipped = false;
    }

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

        if (board.getPiece(forward) == null) {
            moves.add(new ChessMove(myPosition, forward, null));
            if (!moved && board.getPiece(forwardTwo) == null) {
                moves.add(new ChessMove(myPosition, forwardTwo, null));
            }
        }

        //TODO: Implement capturing

        return moves;
    }
}
