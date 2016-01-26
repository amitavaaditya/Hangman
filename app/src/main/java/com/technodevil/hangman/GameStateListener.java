package com.technodevil.hangman;

/**
 * Interface for fragment to activity interaction
 */
public interface GameStateListener {
    void onGameFinished(boolean wonGame, String mysteryWord);
    void onGoodGuess();
    void onBadGuess();
    void onGuessedAlready();
}
