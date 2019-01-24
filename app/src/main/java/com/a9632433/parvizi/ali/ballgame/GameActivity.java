package com.a9632433.parvizi.ali.ballgame;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class GameActivity extends AppCompatActivity {
    LayoutInflater layoutInflater;

    // current score
    private MainGameView currentGame;

    // all scores
    private ArrayList<Integer> scores;

    // root view group
    ViewGroup root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // main game root
        root = findViewById(R.id.mainRoot);

        // init layout inflater
        layoutInflater = getLayoutInflater();


        createNewGameInstance(1);
    }

    public void createNewGameInstance(int level){

        // inflate the game layout
        final View gameView = layoutInflater.inflate(R.layout.game_play_layout,root,false);



        // add the game layout to the root layout
        root.addView(gameView);

        final FrameLayout gameContainer = gameView.findViewById(R.id.gameContainer);               // "game canvas"
        final TextView score = gameView.findViewById(R.id.score);                                  // game score text view
        //currentGame = new MainGameView(this,10,level);                              // a game instance



        try {
            FileInputStream fin = openFileInput("game.txt");
            ObjectInputStream iin = new ObjectInputStream(fin);
            GameState gameState = (GameState) iin.readObject();
            Toast.makeText(getApplicationContext(), gameState.balls.size() +"", Toast.LENGTH_SHORT).show();

            currentGame = new MainGameView(getApplicationContext(), gameState);
            iin.close();
            fin.close();
            Toast.makeText(getApplicationContext(), "Done loading", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IO exception", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Toast.makeText(getApplicationContext(),"Class not found" , Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

        // add the game to the game container
        gameContainer.addView(currentGame);

        // update the score and check for any final result
        final Timer checker = new Timer();
        checker.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // update the score
                        score.setText(String.format("%d", currentGame.getScore()));

                        if (currentGame.isGameEnded()){    // the game is finished
                            if (!currentGame.getFinalStatus()) {   // if player lost the game
                                // remove this game instance
                                root.removeView(gameView);
//                                try {
//                                    FileOutputStream fout = openFileOutput("game.txt", Context.MODE_PRIVATE);
//                                    ObjectOutputStream oout = new ObjectOutputStream(fout);
//                                    oout.writeObject(currentGame.getGameState());
//                                    oout.close();
//                                    fout.close();
//                                    Toast.makeText(getApplicationContext(), "State Saved", Toast.LENGTH_SHORT).show();
//
//
//                                } catch (FileNotFoundException e) {
//                                    Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_SHORT).show();
//                                    e.printStackTrace();
//                                } catch (IOException e) {
//                                    Toast.makeText(getApplicationContext(), "IO exception", Toast.LENGTH_SHORT).show();
//
//                                    e.printStackTrace();
//                                }
                                // cancel this timer
                                checker.cancel();

                                // show lost page
                                createLostPage();

                                // show high score list
                            } else {    // if player won the game

                            }
                        }
                    }
                });
            }
        },0,100);


    }

    public void createLostPage(){

        // inflate the lost page
        final View lostPage = layoutInflater.inflate(R.layout.game_lost_layout,root,false);
        root.addView(lostPage);

        // current score in lost page
        TextView currentScoreText = lostPage.findViewById(R.id.currentScoreLost);
        currentScoreText.setText(currentGame.getScore() + "");

        // replay button
        final ImageButton replayButton = lostPage.findViewById(R.id.replayGameBtn);

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // detach the click listener
                replayButton.setOnClickListener(null);

                // remove the lost page view
                root.removeView(lostPage);

                // create a new game instance
                createNewGameInstance(1);
            }
        });

    }
}
