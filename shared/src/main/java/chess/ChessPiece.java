package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {


    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
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

    /**
     * Checks to see if a move is valid based only of the square itself and not the spaces between the piece
     * and the new move
     *
     * @return
     */
    public Boolean isSquareValid(ChessPosition newMove, ChessPiece myPiece, ChessBoard board){
        if (newMove.getColumn() >= 1 && newMove.getColumn() <= 8){
            if (newMove.getRow() >= 1 && newMove.getRow() <= 8){
                ChessPiece opponent = board.getPiece(newMove);
                if (opponent == null){
                    return true;
                }
                else if (myPiece.getTeamColor() != opponent.getTeamColor()){
                    return true;
                }
            }
        }
        return false;
    }

    public void brqHelper(int[] direction, ChessPosition startPos, ChessBoard board, List<ChessMove> moves){
        ChessPiece piece = board.getPiece(startPos);
        ChessPosition currPos = startPos;
        ChessPosition nextPos = new ChessPosition(currPos.getRow()+direction[0], currPos.getColumn()+direction[1]);
        while (isSquareValid(nextPos, piece, board) && (board.getPiece(currPos) == null  || currPos == startPos)){
            moves.add(new ChessMove(startPos, nextPos, null));
            currPos = nextPos;
            nextPos = new ChessPosition(currPos.getRow()+direction[0], currPos.getColumn()+direction[1]);
        }
    }

    public void bishopMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        ChessPiece piece = board.getPiece(myPosition);
        int[][] directions = {{1,1},{1,-1},{-1,-1},{-1,1}};
        for (int[] direction : directions){
            brqHelper(direction, myPosition, board, moves);
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
       ChessPiece piece = board.getPiece(myPosition);
       List<ChessMove> moves = new ArrayList<>();
       if (piece.getPieceType() == PieceType.BISHOP) {
           bishopMoves(board, myPosition, moves);
           return moves;
       }
       return null;
    }
}
