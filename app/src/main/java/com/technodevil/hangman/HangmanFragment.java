package com.technodevil.hangman;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


/**
 * Fragment responsible for the Game session
 */
public class HangmanFragment extends Fragment {
    //Debugging
    private static final String TAG = "HangmanFragment";
    private static final boolean D = true;

    private static final String FILENAMES[] = {
            "words_easy.txt",
            "words_medium.txt",
            "words_hard.txt",
            "words_expert.txt"
    };

    public static final String LEVEL = "level";
    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;
    public static final int EXPERT = 3;

    private TextView wordView;
    private TextView countView;

    private String mysteryWord;
    StringBuilder currentGuess;
    ArrayList<Character> previousGuesses = new ArrayList<>();


    ArrayList<String> dictionary = new ArrayList<>();

    int maxTries = 6;
    int currentTries = 0;
    private GameStateListener gameStateListener;
    private int level;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (D) Log.d (TAG, "onAttach()");
        if (context instanceof GameStateListener) {
            gameStateListener = (GameStateListener) context;
        } else {
            throw new IllegalArgumentException(getActivity().toString()
                    + "must implement GameStateListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.d (TAG, "onCreate()");
        setRetainInstance(true);
        Bundle bundle = getArguments();
        level = bundle.getInt(LEVEL);
        initilizeStreams();
        mysteryWord = pickWord();
        Log.d(TAG, "mysteryWord: " + mysteryWord);
        currentGuess = initializeGuesses();
        Log.d(TAG, "currentGuess: " + currentGuess);
    }

    private void initilizeStreams() {
        if (D) Log.d (TAG, "initilizeStreams()");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getActivity()
                    .getAssets().open(FILENAMES[level])));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                dictionary.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String pickWord() {
        if (D) Log.d (TAG, "pickWord()");
        Random random = new Random();
        int wordIndex = Math.abs(random.nextInt()) % dictionary.size();
        return dictionary.get(wordIndex).toUpperCase();
    }

    private StringBuilder initializeGuesses() {
        if (D) Log.d (TAG, "initializeGuesses()");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i < mysteryWord.length() * 2; i++) {
            if (i % 2 == 0)
                stringBuilder.append("_");
            else
                stringBuilder.append(" ");
        }
        return stringBuilder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (D) Log.d (TAG, "onCreateView()");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hangman, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (D) Log.d (TAG, "onViewCreated()");
        wordView = (TextView) view.findViewById(R.id.wordView);
        countView = (TextView) view.findViewById(R.id.countView);
        wordView.setText(getFormalCurrentGuess());
        countView.setText(String.format("%s%d",getString(R.string.chances_remaining), maxTries - currentTries));
    }

    private String getFormalCurrentGuess() {
        return currentGuess.toString();
    }

    private boolean checkIfGameOver() {
        if (D) Log.d (TAG, "gameOver()");
        if (didWeWin()) {
            gameStateListener.onGameFinished(true, mysteryWord);
        } else if (didWeLose()) {
            gameStateListener.onGameFinished(false, mysteryWord);
        }
        return false;
    }

    private boolean didWeWin() {
        String guess = getCondensedCurrentGuess();
        return mysteryWord.equals(guess);
    }

    private boolean didWeLose() {
        return currentTries >= maxTries;
    }

    private String getCondensedCurrentGuess() {
        String guess = currentGuess.toString();
        return guess.replace(" ","");
    }

    public void characterClicked(char clickedCharacter) {
        if (D) Log.d (TAG, "characterClicked():" + clickedCharacter);
        if (isGuessedAlready(clickedCharacter))
            gameStateListener.onGuessedAlready();
        else {
            previousGuesses.add(clickedCharacter);
            if(isItAGoodGuess(clickedCharacter)) {
                gameStateListener.onGoodGuess();
                wordView.setText(getFormalCurrentGuess());
            }
            else {
                gameStateListener.onBadGuess();
                currentTries++;
                countView.setText(String.format("%s%d",getString(R.string.chances_remaining), maxTries - currentTries));
            }
            checkIfGameOver();
        }

    }

    private boolean isGuessedAlready(char clickedCharacter) {
        return previousGuesses.contains(clickedCharacter);
    }

    private boolean isItAGoodGuess(char clickedCharacter) {
        boolean isItAGoodGuess = false;
        for (int i = 0; i < mysteryWord.length(); i++) {
            if (mysteryWord.charAt(i) == clickedCharacter) {
                currentGuess.setCharAt(i * 2, clickedCharacter);
                isItAGoodGuess = true;
                wordView.setText(getFormalCurrentGuess());
            }
        }
        return isItAGoodGuess;
    }
}
