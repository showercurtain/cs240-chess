package extra;

import chess.AbstractChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import passoff.chess.TestUtilities;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CastleStateTests {
    @Test
    public void castleStateTest() {
        ChessGame game = new ChessGame();

        Assertions.assertEquals(game.getBoard().getCastleStates(), EnumSet.allOf(AbstractChessBoard.CastleState.class));

        game.getBoard().addPiece(new ChessPosition(1,2), null);
        game.getBoard().addPiece(new ChessPosition(1,3), null);
        game.getBoard().addPiece(new ChessPosition(1,4), null);
        game.getBoard().addPiece(new ChessPosition(1,6), null);
        game.getBoard().addPiece(new ChessPosition(1,7), null);

        var validMoves = TestUtilities.loadMoves(new ChessPosition(1,5), new int[][]{
                {1, 6},
                {1, 7},
                {1, 4},
                {1, 3},
        });

        assertMoves(game, validMoves, new ChessPosition(1, 5));

        game.getBoard().addPiece(new ChessPosition(8,2), null);
        game.getBoard().addPiece(new ChessPosition(8,3), null);
        game.getBoard().addPiece(new ChessPosition(8,4), null);
        game.getBoard().addPiece(new ChessPosition(8,6), null);
        game.getBoard().addPiece(new ChessPosition(8,7), null);

        validMoves = TestUtilities.loadMoves(new ChessPosition(8,5), new int[][]{
                {8, 6},
                {8, 7},
                {8, 4},
                {8, 3},
        });

        assertMoves(game, validMoves, new ChessPosition(8, 5));


    }

    private static void assertMoves(ChessGame game, List<ChessMove> validMoves, ChessPosition position) {
        var generatedMoves = game.validMoves(position);
        var actualMoves = new ArrayList<>(generatedMoves);
        TestUtilities.validateMoves(validMoves, actualMoves);
    }
}
