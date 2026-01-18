package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public interface RayMovingPiece extends MovingChessPiece {
    int[] getRayDirections();

    @Override
    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> out = new ArrayList<>();
        for (int ray : getRayDirections()) {
            for (ChessPosition move : myPosition.getRay(ray)) {
                ChessPiece piece = board.getPiece(move);
                if (piece != null) {
                    if (!piece.getTeamColor().equals(color)) {
                        out.add(new ChessMove(myPosition, move, null));
                    }
                    break;
                }
                out.add(new ChessMove(myPosition, move, null));
            }
        }

        return out;
    }

    @Override
    default Collection<ChessMove> captureMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        int[] rayList = getRayDirections();
        ArrayList<ChessMove> out = new ArrayList<>(rayList.length);
        for (int ray : rayList) {
            for (ChessPosition move : myPosition.getRay(ray)) {
                ChessPiece piece = board.getPiece(move);
                if (piece != null) {
                    if (!piece.getTeamColor().equals(color)) {
                        out.add(new ChessMove(myPosition, move, null));
                    }
                    break;
                }
            }
        }

        return out;
    }
}
