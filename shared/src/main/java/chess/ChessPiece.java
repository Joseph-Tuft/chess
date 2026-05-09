package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

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

    public static Map<PieceType, int[][]> DIRECTIONS = Map.of(
            PieceType.KING, new int[][]{
                    {1,0},{1,-1},{1,1},{0,1},
                    {0,-1},{-1,0},{-1,-1},{-1,1}},
            PieceType.QUEEN, new int[][]{
                    {1,0},{1,-1},{1,1},{0,1},
                    {0,-1},{-1,0},{-1,-1},{-1,1}},
            PieceType.BISHOP, new int[][]{
                    {1,1},{1,-1},{-1,1},{-1,-1}},
            PieceType.KNIGHT, new int[][]{
                    {2,1},{2,-1},{1,2},{1,-2},
                    {-2,1},{-2,-1},{-1,2},{-1,-2}},
            PieceType.ROOK, new int[][]{
                    {1,0},{-1,0},{0,1},{0,-1}},
            PieceType.PAWN, new int[][]{
                    {1,0},{2,0},{1,1},{1,-1}}
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
    public Boolean isSquareValid(ChessBoard board, ChessPosition startPos, ChessPosition nextPos){
        ChessPiece piece = board.getPiece(startPos);
        ChessGame.TeamColor color = piece.getTeamColor();
        int row = nextPos.getRow();
        int col = nextPos.getColumn();
        if (row > 0 && row <= 8 && col > 0 && col <= 8) {
            ChessPiece opponent = board.getPiece(nextPos);
            if (opponent == null) {
                return true;
            } else return opponent.getTeamColor() != color;
        }
        return false;
    }

    /**
     * Updates moves list to contain all the moves for a rook, bishop, or queen
     *
     */
    public void brqMoves(int[][] directions, ChessBoard board, ChessPosition startPos, ArrayList<ChessMove> moves){
        ChessPosition currPos;
        ChessPosition nextPos;
        for (int[] d : directions){
            currPos = startPos;
            nextPos = new ChessPosition(currPos.getRow()+d[0], currPos.getColumn()+d[1]);
            while(isSquareValid(board, startPos, nextPos) && (board.getPiece(currPos)==null || currPos==startPos)){
                moves.add(new ChessMove(startPos, nextPos, null));
                currPos = nextPos;
                nextPos = new ChessPosition(currPos.getRow()+d[0], currPos.getColumn()+d[1]);
            }
        }
    }

    /**
     * Updates moves list to contain all the moves for a king or knight
     *
     */
    public void knMoves(int[][] directions, ChessBoard board, ChessPosition startPos, ArrayList<ChessMove> moves){
        ChessPosition nextPos;
        for (int[] d: directions){
            nextPos = new ChessPosition(startPos.getRow()+d[0], startPos.getColumn()+d[1]);
            if (isSquareValid(board, startPos, nextPos)){
                moves.add(new ChessMove(startPos, nextPos, null));
            }
        }
    }

    public Boolean isFirstMove(ChessBoard board, ChessPosition startPos){
        ChessPiece piece = board.getPiece(startPos);
        ChessGame.TeamColor color = piece.getTeamColor();
        return (color == ChessGame.TeamColor.WHITE && startPos.getRow() == 2) || (color == ChessGame.TeamColor.BLACK && startPos.getRow() == 7);
    }

    /**
     * Checks if the pawn is moving to the end of the board. If so, allows promotions otherwise adds
     * move without promotion.
     * ASSUMES move has already been checked to be a valid move.
     */
    public void checkPromoteAndAdd(ChessBoard board, ChessPosition startPos, ChessPosition nextPos, ArrayList<ChessMove> moves){
        ChessPiece piece = board.getPiece(startPos);
        ChessGame.TeamColor color = piece.getTeamColor();
        int row = nextPos.getRow();
        if ((color == ChessGame.TeamColor.BLACK && row ==1) ||(color == ChessGame.TeamColor.WHITE && row ==8)){
            moves.add(new ChessMove(startPos, nextPos, PieceType.BISHOP));
            moves.add(new ChessMove(startPos, nextPos, PieceType.QUEEN));
            moves.add(new ChessMove(startPos, nextPos, PieceType.KNIGHT));
            moves.add(new ChessMove(startPos, nextPos, PieceType.ROOK));
        } else {
            moves.add(new ChessMove(startPos, nextPos, null));
        }
    }

    /**
     * Updates moves list to contain all the moves for a pawn
     *
     */
    public void pawnMoves(int[][] directions, ChessBoard board, ChessPosition startPos, ArrayList<ChessMove> moves){
        ChessPiece piece = board.getPiece(startPos);
        ChessGame.TeamColor color = piece.getTeamColor();
        if(color == ChessGame.TeamColor.BLACK){
            directions = new int[][] {{-1,0},{-2,0},{-1,1},{-1,-1}};
        }
        for (int[] d : directions){
            ChessPosition nextPos = new ChessPosition(startPos.getRow()+d[0], startPos.getColumn()+d[1]);
            if(isSquareValid(board, startPos, nextPos)){
                //Move forward
                if(d[1] == 0){
                    if(Math.abs(d[0])==2 && isFirstMove(board, startPos) && board.getPiece(nextPos)==null){
                        ChessPosition intermediate = new ChessPosition(startPos.getRow()+d[0]/2, startPos.getColumn()+d[1]);
                        if (board.getPiece(intermediate)==null){
                            moves.add(new ChessMove(startPos, nextPos, null));
                        }
                    }
                    if (Math.abs(d[0])==1 && board.getPiece(nextPos)==null){
                        checkPromoteAndAdd(board, startPos, nextPos, moves);
                    }
                }
                else if (board.getPiece(nextPos) != null){
                    checkPromoteAndAdd(board, startPos, nextPos, moves);
                }
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
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        PieceType type = piece.getPieceType();
        int [][] directions = DIRECTIONS.get(type);
        if(type == PieceType.QUEEN || type == PieceType.BISHOP || type == PieceType.ROOK){
            brqMoves(directions, board, myPosition, moves);
        }
        if(type == PieceType.KING || type == PieceType.KNIGHT){
            knMoves(directions, board, myPosition, moves);
        }
        if(type == PieceType.PAWN){
            pawnMoves(directions, board, myPosition, moves);
        }



        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
