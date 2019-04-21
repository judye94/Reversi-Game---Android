package com.example.judithehiguese.reversigame;

/**
 * Created by judithehiguese on 21/03/2017.
 * This implements the graphical display of the Reversi board.
 */

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.widget.Toast;
import android.util.AttributeSet;


//class definition
public class BoardView extends View {
    //hardcoded assumptions that can work for the emulator
    int BOARD_SCREEN_SIZE = 500;
    int BOARD_DIMS = 8; //dimension
    int CELL_SIZE = BOARD_SCREEN_SIZE/BOARD_DIMS; //split cell size on board
    int PIECE_RADIUS = 4*CELL_SIZE /10; //radius size for pieces
    int CELL_PADDING = (CELL_SIZE)/2; //move piece inches from corner of the cell

    Paint paint = new Paint();
    GameState state;
    Context context;

    public BoardView(Context context) {
        super(context);
        init();
        Log.e("BoardView", "Starting");
        this.context = context;
        state = new GameState(BOARD_DIMS, this);

        // listen to touch events so we can handle the user's move.
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // on touch down, calculate what square the user tried to touch.
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = (int) (event.getX() * BOARD_DIMS / BOARD_SCREEN_SIZE);
                    int y = (int) (event.getY() * BOARD_DIMS / BOARD_SCREEN_SIZE);
                    if (x >= BOARD_DIMS || y >= BOARD_DIMS || x<0 || y<0) {
                        return false;
                    }
                    //pass the board touch on
                    handleUserMove(x, y);
                    return true;
                }
                return false;
            }
        });
    }

    // constructor that takes in a context and also a list of attributes
    // that were set through XML
    public BoardView(Context context, AttributeSet as) {
        super(context, as);
        init();
    }

    // constructor that take in a context, attribute set and also a default // style in case the view is to be styled in a certian way
    public BoardView(Context context, AttributeSet as, int default_style) {
        super(context, as, default_style);
        init(); }

    // refactored init method as most of this code is shared by all the // constructors
    private void init() {
    }




    //handle users move on attempt
    public void handleUserMove(int x, int y) {
        Log.v("BoardView", "User attempted to move at " + x + ", " + y);


        //notifying the user if the square selected is valid (unoccupied would result in flips)
        int captured = state.move(x, y, true);
        if (captured == 0) {
            Log.e("BoardView", "User moves at " + x + ", " + y + " was not valid");
            Toast.makeText(this.context, R.string.toast_cant_move_there, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("BoardView", "User's move at " + x + "," + y + " was valid w/take of " + captured + " piece(s)");

        // TODO: just invalidate the screen area around the flipped/new pieces?
        this.invalidate();

        state.nextTurn(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("BoardView", "onMeasure called with " + widthMeasureSpec + "x" + heightMeasureSpec);

        // The square board should fully fill the smaller dimension
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (parentHeight > parentWidth) {
            BOARD_SCREEN_SIZE = parentWidth;
        } else {
            BOARD_SCREEN_SIZE = parentHeight;
        }

        Log.i("BoardView", "Board size of " + BOARD_SCREEN_SIZE);
        this.setMeasuredDimension(BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE);
        CELL_SIZE = BOARD_SCREEN_SIZE/BOARD_DIMS;
        PIECE_RADIUS = 4 * CELL_SIZE / 10;
        CELL_PADDING = CELL_SIZE / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int i, j;

        //draw vertical board lines
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2); //strength of line

        for (i = 0; i<BOARD_DIMS; i++) {
            canvas.drawLine(i*CELL_SIZE, 0, i*CELL_SIZE, BOARD_SCREEN_SIZE, paint);
        }
        canvas.drawLine(BOARD_SCREEN_SIZE, 0, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, paint);

        //draw horizontal board lines
        for (i=0; i<BOARD_DIMS; i++) {
            canvas.drawLine(0, i*CELL_SIZE, BOARD_SCREEN_SIZE, i*CELL_SIZE, paint);
        }
        canvas.drawLine(0, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, BOARD_SCREEN_SIZE, paint);

        //draw pieces for the board
        for (i=0; i<BOARD_DIMS; i++) {
            for (j=0; j<BOARD_DIMS; j++) {
                ReversiSideType piece = state.board[i][j];
                if (piece == ReversiSideType.WHITE || piece == ReversiSideType.BLACK) {
                    if (piece == ReversiSideType.WHITE) {
                        paint.setColor(Color.WHITE);
                    }
                    if (piece == ReversiSideType.BLACK) {
                        paint.setColor(Color.BLACK);
                    }
                    canvas.drawCircle(
                            (i * CELL_SIZE) + CELL_PADDING,
                            (j * CELL_SIZE) + CELL_PADDING,
                            PIECE_RADIUS, //radius
                            paint);
                }
            }
        }

    }
}
