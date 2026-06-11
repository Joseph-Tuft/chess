package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;


import chess.*;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DataAccessException;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_ROOK;

public class DisplayGame {
    private ChessGame game;
    private ChessBoard board;
    private ChessGame.TeamColor color;

    public DisplayGame(ChessGame game, ChessGame.TeamColor color){
        this.color = color;
        this.game = game;
        this.board = new ChessBoard(game.board);

    }

    public void displayGame(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaderRow(out);
        drawBody(out);
        drawHeaderRow(out);
    }

    public void highlightGame(ChessPosition pos){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        if (board.getPiece(pos) == null){
            displayGame();
            return;
        }
        Collection<ChessMove> validMoves = game.validMoves(pos);
        Collection<ChessPosition> validPositions = validMoves.stream()
                .map(ChessMove::getEndPosition)
                .toList();
        drawHeaderRow(out);
        drawHighlightedBody(out, validPositions, pos);
        drawHeaderRow(out);
    }

    private void drawHeaderRow(PrintStream out){
        out.print(SET_BG_COLOR_DARK_BROWN);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(EMPTY);
        if (color == ChessGame.TeamColor.WHITE) {
            out.print(" a  b  c  d  e  f  g  h ");
        } else {
            out.print(" h  g  f  e  d  c  b  a ");
        }
        out.print(EMPTY);
        out.println(SET_BG_COLOR_WHITE);
    }

    private void configureSquare(PrintStream out, int i, int j, Collection<ChessPosition> positions, ChessPosition startPos){

        int row;
        int col;

        if(color == ChessGame.TeamColor.WHITE){
            row = 8 - i;
            col = j + 1;
        } else {
            row = i + 1;
            col = 8 - j;
        }

        ChessPosition pos = new ChessPosition(row, col);

        if ((i + j) % 2 == 0) {
            out.print(SET_BG_COLOR_LIGHT_BROWN);
            if (positions != null && positions.contains(pos)){
                out.print(SET_BG_COLOR_LIGHT_BLUE);
            }
        } else {
            out.print(SET_BG_COLOR_DARK_BROWN);
            if (positions != null && positions.contains(pos)){
                out.print(SET_BG_COLOR_DARK_BLUE);
            }
        }
        if (positions != null && startPos != null && startPos.equals(pos)){
            out.print(SET_BG_COLOR_YELLOW);
            out.print(SET_TEXT_COLOR_BLACK);
        }

    }

    private void drawBody(PrintStream out){
        drawHighlightedBody(out, null, null);
    }

    private void drawHighlightedBody(PrintStream out, Collection<ChessPosition> positions, ChessPosition startPos) {
        for (int i = 0; i < 8; i++) {
            out.print(SET_BG_COLOR_DARK_BROWN);
            out.print(SET_TEXT_COLOR_WHITE);
            if (color.equals(ChessGame.TeamColor.WHITE)) {
                out.print(String.format(" %d ", 8 - i));
            } else {
                out.print(String.format(" %d ", i+1));
            }

            for (int j = 0; j < 8; j++) {
                configureSquare(out, i, j, positions, startPos);

                int row = (color == ChessGame.TeamColor.WHITE) ? (7 - i) : i;
                int col = (color == ChessGame.TeamColor.WHITE) ? j : (7 - j);

                if (board.board[row][col] == null) {
                    out.print(EMPTY);
                } else {
                    drawPiece(out, row, col);
                    out.print(SET_TEXT_COLOR_WHITE);
                }
            }

            out.print(SET_BG_COLOR_DARK_BROWN);
            out.print(SET_TEXT_COLOR_WHITE);
            if (color.equals(ChessGame.TeamColor.WHITE)) {
                out.print(String.format(" %d ", 8 - i));
            } else {
                out.print(String.format(" %d ", i+1));
            }
            out.println(SET_BG_COLOR_WHITE);
        }
    }

    private void drawPiece(PrintStream out, int i, int j){
            ChessGame.TeamColor color = board.board[i][j].getTeamColor();
            switch (color){
            case ChessGame.TeamColor.BLACK -> {
                switch (board.board[i][j].type){
                case ChessPiece.PieceType.KING -> out.print(BLACK_KING);
                case ChessPiece.PieceType.QUEEN -> out.print(BLACK_QUEEN);
                case ChessPiece.PieceType.ROOK -> out.print(BLACK_ROOK);
                case ChessPiece.PieceType.BISHOP -> out.print(BLACK_BISHOP);
                case ChessPiece.PieceType.KNIGHT -> out.print(BLACK_KNIGHT);
                case ChessPiece.PieceType.PAWN -> out.print(BLACK_PAWN);
            }
            }
            case ChessGame.TeamColor.WHITE -> {
                switch (board.board[i][j].type){
                    case ChessPiece.PieceType.KING -> out.print(WHITE_KING);
                    case ChessPiece.PieceType.QUEEN -> out.print(WHITE_QUEEN);
                    case ChessPiece.PieceType.ROOK -> out.print(WHITE_ROOK);
                    case ChessPiece.PieceType.BISHOP -> out.print(WHITE_BISHOP);
                    case ChessPiece.PieceType.KNIGHT -> out.print(WHITE_KNIGHT);
                    case ChessPiece.PieceType.PAWN -> out.print(WHITE_PAWN);
                }
            }
        }

    }
}
