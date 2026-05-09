package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board = new ChessPiece[8][8];
    public ChessPosition whiteKing = new ChessPosition(1,5);
    public ChessPosition blackKing = new ChessPosition(8,5);

    public ChessBoard() {
        
    }

    public ChessBoard(ChessBoard board) {
        for (int i = 0; i<8; i++){
            System.arraycopy(board.board[i], 0, this.board[i], 0, 8);
        }
        this.blackKing = board.blackKing;
        this.whiteKing = board.whiteKing;
    }

    public void setKingPos (ChessGame.TeamColor turn, ChessPosition endPosition){
        if (turn == ChessGame.TeamColor.BLACK){
            blackKing = endPosition;
        } else {
            whiteKing = endPosition;
        }
    }

    public ChessPosition getKingPos (ChessGame.TeamColor turn){
        if(turn == ChessGame.TeamColor.BLACK){
            return blackKing;
        } else { return whiteKing;}
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    public void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    public void clearBoard(){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                board[i][j] = null;
            }
        }
    }

    public void placePawnRow(ChessGame.TeamColor color){
        int row;

        if (color == ChessGame.TeamColor.BLACK){row = 7;}
        else {row = 2;}
        for (int col = 1; col <= 8; col ++){
            addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    public void placeBackRow(ChessGame.TeamColor color){
        int row;
        if (color == ChessGame.TeamColor.BLACK){row = 8;}
        else {row = 1;}
        for (int col = 1; col <= 9; col ++){
            if (col == 1 || col == 8){
                addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.ROOK));
            }
            if (col == 2 || col == 7){
                addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
            }
            if (col == 3 || col == 6){
                addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
            }
            if (col == 4){
                addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
            }
            if (col == 5){
                addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.KING));
            }
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clearBoard();
        placePawnRow(ChessGame.TeamColor.BLACK);
        placePawnRow(ChessGame.TeamColor.WHITE);
        placeBackRow(ChessGame.TeamColor.BLACK);
        placeBackRow(ChessGame.TeamColor.WHITE);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
