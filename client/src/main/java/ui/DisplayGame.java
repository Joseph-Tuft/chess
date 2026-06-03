package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.WHITE_PAWN;
import static ui.EscapeSequences.WHITE_ROOK;

public class DisplayGame {
    private ChessBoard board;
    private ChessGame.TeamColor color;

    public DisplayGame(ChessGame game, ChessGame.TeamColor color){
        this.color = color;
        this.board = new ChessBoard(game.board);
        if (color == ChessGame.TeamColor.BLACK){
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    this.board.board[i][j] = game.board.board[7-i][7-j];
                }
            }
        }
    }

    public void displayGame(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        drawHeaderRow(out);
        drawBody(out);
        drawHeaderRow(out);
    }

    private void drawHeaderRow(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
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

    private void drawBody(PrintStream out) {
        for (int i = 0; i < 8; i++) {
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(String.format(" %d ", 8 - i));

            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    out.print(SET_BG_COLOR_CYAN);
                } else {
                    out.print(SET_BG_COLOR_MAGENTA);
                }
                if (board.board[i][j] == null) {
                    out.print(EMPTY);
                } else {
                    drawPiece(out, i, j);
                }
            }

            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(String.format(" %d ", 8 - i));
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
