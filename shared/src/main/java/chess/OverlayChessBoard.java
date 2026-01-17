package chess;

import java.util.HashMap;
import java.util.Map;

/**
 * A temporary overlay of a base board
 * An overlay board is only valid for as long as its base doesn't change. There is no logic to make sure of this
 */
public class OverlayChessBoard extends AbstractChessBoard {
    AbstractChessBoard base;
    HashMap<ChessPosition, ChessPiece> changes;

    public OverlayChessBoard(AbstractChessBoard base) {
        super();
        this.base = base;
        changes = new HashMap<>();
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        changes.put(position, piece);
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        if (changes.containsKey(position)) return changes.get(position);
        return base.getPiece(position);
    }

    @Override
    public void clearBoard() {
        // This probably shouldn't be called on an overlay board...

        changes = new HashMap<>();
        for (ChessPosition position : base.getPieces().keySet()) {
            changes.put(position, null);
        }
    }

    @Override
    public Map<ChessPosition, ChessPiece> getPieces() {
        HashMap<ChessPosition, ChessPiece> out = new HashMap<>();
        out.putAll(base.getPieces());
        out.putAll(changes);
        return out;
    }
}
