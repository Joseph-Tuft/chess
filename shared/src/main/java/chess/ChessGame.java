package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();


    public ChessGame() {
        board.resetBoard();
    }



    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition, ChessBoard board) {
        ChessPiece piece = board.getPiece(startPosition);
        return piece.pieceMoves(board, startPosition);
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return validMoves(startPosition, this.board);
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move, ChessBoard board) throws InvalidMoveException {
        Collection<ChessMove> valid = validMoves(move.getStartPosition(), board);
        if (valid.contains(move)){
            ChessPiece piece = board.getPiece(move.getStartPosition());
            ChessPiece opponent = board.getPiece(move.getEndPosition());
            board.removePiece(move.getStartPosition());
            board.addPiece(move.getEndPosition(), piece);
            if (piece.getPieceType() == ChessPiece.PieceType.KING){
                board.setKingPos(turn, move.getEndPosition());
            }
            if(isInCheck(turn, board)){
                board.addPiece(move.getStartPosition(), piece);
                board.addPiece(move.getEndPosition(), opponent);
                if (piece.getPieceType() == ChessPiece.PieceType.KING){
                    board.setKingPos(turn, move.getStartPosition());
                }
                throw new InvalidMoveException();
            }
            if(turn == TeamColor.BLACK){
                turn = TeamColor.WHITE;
            }else {turn = TeamColor.BLACK;}
        }
        else{throw new InvalidMoveException();}
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        makeMove(move, this.board);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingPos = board.getKingPos(teamColor);
        for(int row = 1; row <=8; row++){
            for(int col = 1; col <=8; col++){
                ChessPosition opponentPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(opponentPos);
                if (piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = piece.pieceMoves(board, opponentPos);
                    if (moves.contains(new ChessMove(opponentPos, kingPos, null)) || moves.contains(new ChessMove(opponentPos, kingPos, ChessPiece.PieceType.QUEEN))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, this.board);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate (TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        for(int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currPos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(currPos);
                    for (ChessMove move : moves){
                        ChessBoard simBoard = new ChessBoard(this.board);
                        try {
                            makeMove(move, simBoard);
                            if (!isInCheck(teamColor, simBoard)) {
                                return false;
                            }
                        }
                        catch (InvalidMoveException e){
                            //continue
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }
        for(int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(currPos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (validMoves(currPos) != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return turn == chessGame.turn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, board);
    }
}
