package com.example.judithehiguese.reversigame;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;

public class MainGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardView boardView = new BoardView(this);
        boardView.setBackgroundColor(Color.RED);
        setContentView(boardView);


    }

}
