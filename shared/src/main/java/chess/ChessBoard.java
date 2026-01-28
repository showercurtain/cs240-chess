package chess;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard extends AbstractChessBoard {
    final ChessPiece[] pieces;

    public ChessBoard() {
        super();
        pieces = new ChessPiece[64];
    }

    private static int indexOf(ChessPosition position) {
        return (position.row()-1) * 8 + (position.col()-1);
    }

    private static ChessPosition positionOf(int index) {
        return new ChessPosition(index/8+1,index%8+1);
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces[indexOf(position)] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[indexOf(position)];
    }

    @Override
    public void clearBoard() {
        Arrays.fill(pieces, null);
    }

    @Override
    public Map<ChessPosition, ChessPiece> getPieces() {
        HashMap<ChessPosition, ChessPiece> out = new HashMap<>();
        for (int i = 0; i < 64; i++) {
            if (pieces[i] != null) {
                out.put(positionOf(i), pieces[i]);
            }
        }
        return out;
    }
}
