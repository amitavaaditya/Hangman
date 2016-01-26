package com.technodevil.hangman;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements GameStateListener {
    //Debugging
    private static final String TAG = "MainActivity";
    private static boolean D = true;

    //CoordinatorLayout instance
    private CoordinatorLayout coordinatorLayout;

    //HangmanFragment instance
    HangmanFragment hangmanFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new InitFragment()).commit();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    }

    /**
     * Called when user clicks on the screen to start game
     */

    public void chooseLevel(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_level_choose);
        dialog.setTitle(R.string.choose_level);
        dialog.show();
        (dialog.findViewById(R.id.buttonEasy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(HangmanFragment.EASY);
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.buttonMedium)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(HangmanFragment.MEDIUM);
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.buttonHard)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(HangmanFragment.HARD);
                dialog.dismiss();
            }
        });
        (dialog.findViewById(R.id.buttonExpert)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(HangmanFragment.EXPERT);
                dialog.dismiss();
            }
        });
    }

    /**
     * called by the choose level dialog to start game
     * @param level level to start game as
     */
    public void startGame(int level) {
        if (D) Log.d(TAG, "startGame():" + level);
        Bundle bundle = new Bundle();
        bundle.putInt(HangmanFragment.LEVEL, level);
        hangmanFragment = new HangmanFragment();
        hangmanFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame, hangmanFragment)
                .commit();
    }

    /**
     * Called when user clicks any of the character buttons
     */
    public void characterClicked(View view) {
        char clickedCharacter = ((Button)view).getText().charAt(0);
        if (D) Log.d(TAG, "characterClicked():" + clickedCharacter);
        hangmanFragment.characterClicked(clickedCharacter);
    }

    @Override
    public void onGameFinished(boolean wonGame, String mysteryWord) {
        if (D) Log.d(TAG, "onGameFinished()" );
        if (wonGame) {
            new AlertDialog.Builder(this)
                    .setTitle("Mystery word : " + mysteryWord)
                    .setMessage(R.string.game_won)
                    .setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, new InitFragment())
                                    .commit();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Mystery word : " + mysteryWord)
                    .setMessage(R.string.game_lost)
                    .setPositiveButton(R.string.play_again, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame, new InitFragment())
                                    .commit();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public void onGoodGuess() {
        if (D) Log.d(TAG, "onGoodGuess()" );
        Snackbar.make(coordinatorLayout, R.string.good_guess, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBadGuess() {
        if (D) Log.d(TAG, "onBadGuess()" );
        Snackbar.make(coordinatorLayout, R.string.bad_guess, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onGuessedAlready() {
        if (D) Log.d(TAG, "onGuessedAlready()" );
        Snackbar.make(coordinatorLayout, R.string.already_guessed, Snackbar.LENGTH_SHORT).show();
    }
}
