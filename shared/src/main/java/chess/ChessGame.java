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
        TeamColor team = piece.getTeamColor();
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> goodMoves = new ArrayList<>();
        for (ChessMove move : moves){
            ChessBoard simBoard = new ChessBoard(board);
            ChessPosition endPosition = move.getEndPosition();
            simBoard.removePiece(startPosition);
            simBoard.addPiece(endPosition, piece);
            if(piece.getPieceType() == ChessPiece.PieceType.KING){
                simBoard.setKingPos(team, endPosition);
            }
            if(!isInCheck(team, simBoard)){
                goodMoves.add(move);
            }
        }
        return goodMoves;
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
        ChessPosition startPos = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPos);
        if(piece == null || piece.getTeamColor() != turn){throw new InvalidMoveException();}
        Collection<ChessMove> valid = validMoves(move.getStartPosition(), board);
        if (valid.contains(move)){
            board.removePiece(startPos);
            board.addPiece(move.getEndPosition(), piece);
            if (piece.getPieceType() == ChessPiece.PieceType.KING){
                board.setKingPos(turn, move.getEndPosition());
            }
            if (move.getPromotionPiece() != null){
                piece.type = move.getPromotionPiece();
            }
        }
        else{throw new InvalidMoveException();}
        if (turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        } else {turn = TeamColor.WHITE;}
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
        board.findKings();
        ChessPosition kingPos = board.getKingPos(teamColor);
        System.out.printf("KingPos: %s%n", kingPos);
        for(int row = 1; row <=8; row++){
            for(int col = 1; col <=8; col++){
                ChessPosition opponentPos = new ChessPosition(row, col);
                ChessPiece opponent = board.getPiece(opponentPos);
                if (opponent != null && opponent.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = opponent.pieceMoves(board, opponentPos);
                    System.out.printf("%s %s moves: %s%n", opponent.getTeamColor(), opponent.getPieceType(), moves);
                    for(ChessMove move : moves){
                        if (move.getEndPosition().equals(kingPos)){
                            return true;
                        }
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
                    if (!moves.isEmpty()) {
                        return false;
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
