package com.example.judithehiguese.reversigame;

/**
 * Created by judithehiguese on 21/03/2017.
 *
 */

import android.util.Log;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class GameState {
    public ReversiSideType board[][];
    public int dims;
    public ReversiSideType currentPlayer;
    public BoardView view;

    // Create a new Game State
    public GameState(int dims, BoardView view){
        this.dims = dims;
        this.view = view;
        board = new ReversiSideType[dims][dims];
        clearBoard();
    }

    // Create a new Game State based on another new game
    public GameState(GameState copy){
        this.dims = copy.dims;
        this.currentPlayer = copy.currentPlayer;
        this.board = new ReversiSideType[dims][dims];
        int i, j;
        for(i=0; i<dims; i++){
            for(j=0; j<dims; j++){
                this.board[i][j] = copy.board[i][j];
            }
        }
    }



    // Initialize the board to a starting state when game is cleared
    public void clearBoard(){

        //empty the board
        int i, j;
        for(i=0; i<dims; i++){
            for(j=0; j<dims; j++){
                board[i][j] = ReversiSideType.EMPTY;
            }
        }

        // place the starting pieces ON THE BOARD
        i = (int)((dims-1)/2);
        board[i][i] = ReversiSideType.WHITE;
        board[i+1][i+1] = ReversiSideType.WHITE;
        board[i][i+1] = ReversiSideType.BLACK;
        board[i+1][i] = ReversiSideType.BLACK;

        // white plays first
        currentPlayer = ReversiSideType.WHITE;

        //redraw the board
        view.invalidate();
    }

    // The game is over! Calculate winner & display / record.
    public void gameOver(){
        int whitePieces = 0;
        int blackPieces = 0;
        int i, j;
        Log.i("GameState", "Game Over, Dude!" );

        //tally the scores
        for(i=0; i<dims; i++){
            for(j=0; j<dims; j++){
                if(board[i][j] == ReversiSideType.WHITE){
                    whitePieces++;
                }
                if(board[i][j] == ReversiSideType.BLACK){
                    blackPieces++;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        if(whitePieces > blackPieces){
            builder.setTitle(R.string.alert_gameover_title_white_wins);
        }
        if(whitePieces < blackPieces){
            builder.setTitle(R.string.alert_gameover_title_black_wins);
        }
        if(whitePieces == blackPieces){
            builder.setTitle(R.string.alert_gameover_title_tie);
        }

        builder.setMessage(view.getContext().getResources().getString(R.string.alert_gameover_text, whitePieces, blackPieces));

        builder.setPositiveButton("New Game", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clearBoard();
            }
        });
        builder.create().show();
        this.currentPlayer = ReversiSideType.EMPTY;
    }


    public void swapSides(){
        assert (currentPlayer != ReversiSideType.EMPTY);
        currentPlayer = (currentPlayer == ReversiSideType.WHITE)? ReversiSideType.BLACK : ReversiSideType.WHITE;
    }

    // The current side has ended their turn.
    // Switch sides and see if the game is over.
    public void nextTurn(boolean lastMoveWasForfeit){

        swapSides();

        Log.i("GameState", "Now it's " + currentPlayer + "'s turn");

        if(!this.isAMovePossible()){
            if(lastMoveWasForfeit){
                Log.i("GameState", "No moves left, ending game");
                this.gameOver();
            }else{
                Log.i("GameState", "Couldn't move! Checking other side.");
                this.nextTurn(true);
            }
        }
    }

    // checks to see if the current player can make any move at all.
    // TODO: return an array of permissible moves instead.
    public boolean isAMovePossible(){
        int i, j;
        for(i=0; i<dims; i++){
            for(j=0; j<dims; j++){
                int numCaptured = this.move(i, j, false);
                if(numCaptured > 0){
                    return true;
                }
            }
        }
        return false;
    }

    // compute whether a given move is valid, and, possibly, do it
    public int move(int i, int j, Boolean doMove){
        //assert (i >= 0);
        //assert (i < dims);
        //assert (j >= 0);
        //assert (j < dims);

        // if the proposed space is already occupied, bail.
        if(board[i][j] != ReversiSideType.EMPTY){
            return 0;
        }

        // explore whether any of the eight 'rays' extending from the current piece
        // have a line of at least one opponent piece terminating in one of our own pieces.

        int dx, dy;
        int totalCaptured = 0;
        for(dx = -1; dx <= 1; dx++){
            for(dy = -1; dy <= 1; dy++){
                // (skip the null movement case)
                if(dx == 0 && dy == 0){ continue; }

                // explore the ray for potential captures
                for(int steps = 1; steps < dims; steps++){
                    int ray_i = i + (dx*steps);
                    int ray_j = j + (dy*steps);

                    // if the ray has gone out of bounds, give up
                    if(ray_i < 0 || ray_i >= dims || ray_j < 0 || ray_j >= dims){ break; }

                    ReversiSideType ray_cell = board[ray_i][ray_j];

                    // if we hit a blank cell before terminating a sequence, give up
                    if(ray_cell == ReversiSideType.EMPTY){break;}

                    // if we hit a piece that's our own, let's capture the sequence
                    if(ray_cell == currentPlayer){
                        if(steps > 1){
                            // we've gone at least one step, capture the ray
                            totalCaptured += steps - 1;
                            if(doMove){ //time to execute
                                while (steps-- > 0){
                                    board[i + (dx*steps)][j + (dy*steps)] = currentPlayer;
                                };
                            }
                        }
                        break;
                    }
                }
            }
        }
        return totalCaptured;
    }

}
