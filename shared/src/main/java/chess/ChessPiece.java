package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    private static final Map<PieceType, int[][]> DIRECTIONS = Map.of(
            PieceType.KING, new int[][]{
                    { 1, 0},{ 1, 1},{ 1,-1},{0, 1},
                    { 0,-1},{-1,-1},{-1, 0},{-1,1}},
            PieceType.QUEEN, new int[][]{
                    {1,1},{1,-1},{-1,-1},{-1,1},
                    {0,1},{0,-1},{-1, 0},{ 1,0}},
            PieceType.ROOK, new int[][]{
                    {0,1},{0,-1},{-1,0},{1,0}},
            PieceType.BISHOP, new int[][]{
                    {1,1},{1,-1},{-1,-1},{-1,1}},
            PieceType.KNIGHT, new int[][]{
                    { 2,1},{ 2,-1},{ 1,2},{ 1,-2},
                    {-1,2},{-1,-2},{-2,1},{-2,-1}},
            PieceType.PAWN, new int[][]{
                    {0,1},{0,2},{1,1},{-1,1}}
    );

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
     * Checks to see if a move is valid based only of the square itself and not the spaces between the piece
     * and the new move
     *
     * @return true or false if the square allows a valid move
     */
    public Boolean isSquareValid(ChessPosition newMove, ChessPiece myPiece, ChessBoard board){
        if (newMove.getColumn() >= 1 && newMove.getColumn() <= 8){
            if (newMove.getRow() >= 1 && newMove.getRow() <= 8){
                ChessPiece opponent = board.getPiece(newMove);
                if (opponent == null){
                    return true;
                }
                else return myPiece.getTeamColor() != opponent.getTeamColor();
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
        int[][] directions = {{1,1},{1,-1},{-1,-1},{-1,1}};
        for (int[] direction : directions){
            brqHelper(direction, myPosition, board, moves);
        }
    }

    public void rookMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        int[][] directions = {{0,1},{0,-1},{-1,0},{1,0}};
        for (int[] direction : directions){
            brqHelper(direction, myPosition, board, moves);
        }
    }

    public void queenMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        int[][] directions = {{1,1},{1,-1},{-1,-1},{-1,1},{0,1},{0,-1},{-1,0},{1,0}};
        for (int[] direction : directions){
            brqHelper(direction, myPosition, board, moves);
        }
    }

    public void knightMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        int[][] directions = {{2,1},{2,-1},{1,2},{1,-2},{-1,2},{-1,-2},{-2,1},{-2,-1}};
        ChessPiece piece = board.getPiece(myPosition);
        for (int[] d : directions){
            ChessPosition newPos = new ChessPosition(myPosition.getRow()+d[0], myPosition.getColumn()+d[1]);
            if (isSquareValid(newPos, piece, board)){
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    public void kingMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        int[][] directions = {{1,1},{1,-1},{-1,-1},{-1,1},{0,1},{0,-1},{-1,0},{1,0}};
        ChessPiece piece = board.getPiece(myPosition);
        for (int[] d : directions){
            ChessPosition newPos = new ChessPosition(myPosition.getRow()+d[0], myPosition.getColumn()+d[1]);
            if (isSquareValid(newPos, piece, board)){
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }

    public void pawnMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> moves){
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();
        int[][] directions;
        if (color == ChessGame.TeamColor.WHITE){
            directions = new int[][] {{1,0},{2,0},{1,1},{1,-1}};
        }
        else {
            directions = new int[][] {{-1,0},{-2,0},{-1,1},{-1,-1}};
        }
        for (int[] d : directions){
            ChessPosition newPos = new ChessPosition(myPosition.getRow()+d[0], myPosition.getColumn()+d[1]);
            //Move forward
            if (d[1]==0 && isSquareValid(newPos, piece, board) && board.getPiece(newPos) == null) {
                    if (Math.abs(d[0]) == 2) {
                        ChessPosition intermediate = new ChessPosition(myPosition.getRow() + d[0] /2, myPosition.getColumn() + d[1]);
                        if (board.getPiece(intermediate) == null) {
                            if (d[0] < 0 && myPosition.getRow() == 7) {
                                moves.add(new ChessMove(myPosition, newPos, null));
                            } else if (d[0] > 0 && myPosition.getRow() == 2) {
                                moves.add(new ChessMove(myPosition, newPos, null));
                            }
                        }
                    } else {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
            //Capture diagonal
            } else if (d[1] != 0 && isSquareValid(newPos, piece, board) && board.getPiece(newPos) != null){
                moves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
       ChessPiece piece = board.getPiece(myPosition);
       List<ChessMove> moves = new ArrayList<>();
       if (piece.getPieceType() == PieceType.BISHOP) {
           bishopMoves(board, myPosition, moves);
           return moves;
       }
       if (piece.getPieceType() == PieceType.ROOK) {
            rookMoves(board, myPosition, moves);
            return moves;
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            queenMoves(board, myPosition, moves);
            return moves;
        }
        if (piece.getPieceType() == PieceType.KNIGHT) {
            knightMoves(board, myPosition, moves);
            return moves;
        }
        if (piece.getPieceType() == PieceType.KING) {
            kingMoves(board, myPosition, moves);
            return moves;
        }
        if (piece.getPieceType() == PieceType.PAWN) {
            pawnMoves(board, myPosition, moves);
            return moves;
        }

       return null;
    }
}
