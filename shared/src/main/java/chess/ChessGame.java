package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    TeamColor teamTurn;
    ChessBoard board;

    // These are for redundancy, to avoid looking for kings every move
    ChessPosition blackKing;
    ChessPosition whiteKing;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
        blackKing = board.findKing(TeamColor.BLACK);
        whiteKing = board.findKing(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private Collection<ChessMove> castleMoves(TeamColor team) {

        EnumSet<AbstractChessBoard.CastleState> state = board.getCastleStates();

        boolean castleQueenside =
                (team == TeamColor.WHITE && state.contains(AbstractChessBoard.CastleState.W_QUEEN)) ||
                (team == TeamColor.BLACK && state.contains(AbstractChessBoard.CastleState.B_QUEEN));

        boolean castleKingside =
                (team == TeamColor.WHITE && state.contains(AbstractChessBoard.CastleState.W_KING)) ||
                (team == TeamColor.BLACK && state.contains(AbstractChessBoard.CastleState.B_KING));

        if (!(castleKingside || castleQueenside)) return List.of();

        int startRow;
        if (team == TeamColor.WHITE) startRow = 1;
        else startRow = 8;

        ChessPosition startPosition = new ChessPosition(startRow, 5);

        if (board.isInDanger(startPosition)) return List.of();

        ChessPosition left1 = startPosition.withOffset(new ChessPosition.Offset(0,-1));
        ChessPosition left2 = startPosition.withOffset(new ChessPosition.Offset(0,-2));
        ChessPosition right1 = startPosition.withOffset(new ChessPosition.Offset(0,1));
        ChessPosition right2 = startPosition.withOffset(new ChessPosition.Offset(0,2));

        ArrayList<ChessMove> out = new ArrayList<>(2);

        OverlayChessBoard overlay = new OverlayChessBoard(board);
        if (castleKingside && board.getPiece(right1) == null && board.getPiece(right2) == null) {
            overlay.movePiece(new ChessMove(startPosition, right1, null));
            if (!overlay.isInDanger(right1)) {
                overlay.resetOverlay();
                overlay.movePiece(new ChessMove(startPosition, right2, null));
                if (!overlay.isInDanger(right2)) {
                    out.add(new ChessMove(startPosition, right2, null));
                }
            }
            overlay.resetOverlay();
        }

        if (castleQueenside && board.getPiece(left1) == null && board.getPiece(left2) == null) {
            overlay.movePiece(new ChessMove(startPosition, left1, null));
            if (!overlay.isInDanger(left1)) {
                overlay.resetOverlay();
                overlay.movePiece(new ChessMove(startPosition, left2, null));
                if (!overlay.isInDanger(left2)) {
                    out.add(new ChessMove(startPosition, left2, null));
                }
            }
            overlay.resetOverlay();
        }

        return out;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ArrayList<ChessMove> valid = new ArrayList<>();
        OverlayChessBoard overlay = new OverlayChessBoard(board);
        ChessPiece piece = board.getPiece(startPosition);

        if (piece.type().equals(ChessPiece.PieceType.KING)) {
            for (ChessMove move : piece.pieceMoves(board, startPosition)) {
                overlay.resetOverlay();
                overlay.movePiece(move);
                if (!overlay.isInDanger(move.endPosition())) {
                    valid.add(move);
                }
            }
            valid.addAll(castleMoves(piece.getTeamColor()));
        } else {
            ChessPosition kingPosition = piece.getTeamColor() == TeamColor.BLACK ? blackKing : whiteKing;

            for (ChessMove move : piece.pieceMoves(board, startPosition)) {
                overlay.resetOverlay();
                overlay.movePiece(move);
                if (!overlay.isInDanger(kingPosition)) {
                    valid.add(move);
                }
            }
        }

        return valid;
    }

    public boolean cannotMove(TeamColor teamColor) {
        OverlayChessBoard overlay = new OverlayChessBoard(board);
        ChessPosition kingPosition = teamColor == TeamColor.BLACK ? blackKing : whiteKing;

        for (ChessMove move : board.getAllMoves(teamColor)) {
            overlay.resetOverlay();
            overlay.movePiece(move);
            if (!overlay.isInDanger(move.startPosition().equals(kingPosition) ? move.endPosition() : kingPosition)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.startPosition());
        if (piece == null) {
            throw new InvalidMoveException("There is no piece to move there");
        }

        boolean castle = piece.getPieceType().equals(ChessPiece.PieceType.KING) &&
                castleMoves(piece.getTeamColor()).contains(move);

        if (!piece.getTeamColor().equals(teamTurn)) {
            throw new InvalidMoveException("Cannot move your opponent's pieces");
        }
        if (!piece.type().validateMove(board, piece.pieceColor(), move) && !castle) {
            throw new InvalidMoveException("That piece cannot move like that");
        }
        ChessPiece destinationPiece = board.getPiece(move.endPosition());
        if (destinationPiece != null && destinationPiece.getTeamColor().equals(piece.getTeamColor())) {
            throw new InvalidMoveException("You cannot capture your own pieces");
        }
        OverlayChessBoard overlay = new OverlayChessBoard(board);
        overlay.movePiece(move);
        ChessPosition kingPosition = teamTurn == TeamColor.BLACK ? blackKing : whiteKing;
        if (move.startPosition().equals(kingPosition)) {
            kingPosition = move.endPosition();
        }
        if (overlay.isInDanger(kingPosition)) {
            throw new InvalidMoveException("That move puts the king in check");
        }
        if (move.promotionPiece() != null && (!piece.type().equals(ChessPiece.PieceType.PAWN) || move.endPosition().row() % 7 != 1)) {
            throw new InvalidMoveException("That piece cannot be promoted");
        }
        board.movePiece(move);
        if (castle) {
            int row = move.startPosition().row();
            int col1;
            int col2;
            if (move.startPosition().col() < move.endPosition().col()) {
                col1 = 8;
                col2 = 6;
            } else {
                col1 = 1;
                col2 = 4;
            }
            board.movePiece(new ChessMove(
                    new ChessPosition(row, col1),
                    new ChessPosition(row, col2), null));
        }
        teamTurn = teamTurn.equals(TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return board.isInDanger(teamColor == TeamColor.BLACK ? blackKing : whiteKing);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && cannotMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && cannotMove(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        blackKing = board.findKing(TeamColor.BLACK);
        whiteKing = board.findKing(TeamColor.WHITE);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof ChessGame other)) return false;
        return other.getBoard().equals(this.getBoard()) && other.getTeamTurn().equals(this.getTeamTurn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
