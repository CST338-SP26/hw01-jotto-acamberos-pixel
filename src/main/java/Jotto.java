/**
 * @author feng3302
 * @version 0.1.0
 * @Since 1/29/26
 **/
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Jotto {
    // Constants
    private static final int WORD_SIZE = 5;
    private static final boolean DEBUG = true;  // Set to true for debugging

    // Instance variables
    private String currentWord;
    private int score;
    private final ArrayList<String> playGuesses = new ArrayList<>();
    private final ArrayList<String> playWords = new ArrayList<>();
    private String filename;
    private final ArrayList<String> wordList = new ArrayList<>();

    // Constructor
    public Jotto(String filename) {
        this.filename = filename;
        readWords();
    }

    // Read words from file
    public ArrayList<String> readWords() {
        try {
            File file = new File(filename);
            Scanner fileScanner = new Scanner(file);

            while (fileScanner.hasNextLine()) {
                String word = fileScanner.nextLine().trim().toLowerCase();
                // Check for duplicates before adding
                if (!wordList.contains(word) && !word.isEmpty()) {
                    wordList.add(word);
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't open " + filename);
        }
        return wordList;
    }

    // Main menu loop
    public void play() {
        Scanner scan = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to the game. Current Score: " + score);
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.println("Choose one of the following:");
            System.out.println("1: Start the game");
            System.out.println("2: See the word list");
            System.out.println("3: See the chosen words");
            System.out.println("4: Show Player guesses");
            System.out.println("zz to exit");
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.print("What is your choice: ");

            String choice = scan.nextLine().trim().toLowerCase();

            if (choice.equals("1") || choice.equals("one")) {
                if (!pickWord()) {
                    showPlayerGuesses();
                } else {
                    score += guess();
                    System.out.println("Your score is " + score);
                }
            } else if (choice.equals("2") || choice.equals("two")) {
                System.out.println(showWordList());
            } else if (choice.equals("3") || choice.equals("three")) {
                System.out.println(showPlayedWords());
            } else if (choice.equals("4") || choice.equals("four")) {
                showPlayerGuesses();
            } else if (choice.equals("zz")) {
                running = false;
                System.out.println("Final score: " + score);
                System.out.println("Thank you for playing");
                continue;  // Skip the "press enter" prompt
            } else {
                System.out.println("I don't know what \"" + choice + "\" is.");
            }

            System.out.println("Press enter to continue");
            scan.nextLine();
        }
    }

    // Select a random word
    public boolean pickWord() {
        Random rand = new Random();

        // Check if all words have been played
        if (playWords.size() >= wordList.size()) {
            System.out.println("You've guessed them all!");
            return false;
        }

        // Pick a random word that hasn't been played
        currentWord = wordList.get(rand.nextInt(wordList.size()));

        // If already played, do again
        if (playWords.contains(currentWord)) {
            return pickWord();
        }

        playWords.add(currentWord);

        if (DEBUG) {
            System.out.println(currentWord);
        }

        return true;
    }

    // Main guessing loop
    public int guess() {
        ArrayList<String> currentGuesses = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        int letterCount = 0;
        int score = WORD_SIZE + 1;
        String wordGuess;

        while (true) {
            System.out.println("Current Score: " + score);
            System.out.print("What is your guess (q to quit):");

            wordGuess = scan.nextLine().trim().toLowerCase();

            // allow option to quit if user types q
            if (wordGuess.equals("q")) {
                score = Math.min(0, score);
                break;
            }

            // Check word length
            if (wordGuess.length() != WORD_SIZE) {
                System.out.println("Word must be " + WORD_SIZE + " characters ("
                        + wordGuess + " is " + wordGuess.length() + ")");
                continue;
            }

            // Add to player guesses
            addPlayerGuess(wordGuess);

            // Check if correct
            if (wordGuess.equals(currentWord)) {
                System.out.println("DINGDINGDING!!! the word was " + currentWord);
                currentGuesses.add(wordGuess);
                playerGuessScores(currentGuesses);
                return score;
            }

            // Check if already guessed this round
            if (currentGuesses.contains(wordGuess)) {
                System.out.println(wordGuess + " has already been entered");
                continue;
            }

            // Add to current round guesses
            currentGuesses.add(wordGuess);

            // Get letter count
            letterCount = getLetterCount(wordGuess);

            if (letterCount == WORD_SIZE) {
                System.out.println(wordGuess + " is an anagram!");
            } else {
                System.out.println(wordGuess + " has a Jotto score of " + letterCount);
            }

            score--;
            playerGuessScores(currentGuesses);
        }

        return score;
    }

    // Count matching letters between guess and current word
    public int getLetterCount(String wordGuess) {
        if (wordGuess.equals(currentWord)) {
            return WORD_SIZE;
        }

        int count = 0;

        // Create a list of unique characters from currentWord
        ArrayList<Character> uniqueChars = new ArrayList<>();
        for (char c : currentWord.toCharArray())
        {
            if (!uniqueChars.contains(c))
            {
                uniqueChars.add(c);
            }
        }

        // Check each character in the guess
        ArrayList<Character> countedChars = new ArrayList<>();
        for (char c : wordGuess.toCharArray())
        {
            if (uniqueChars.contains(c) && !countedChars.contains(c))
            {
                count++;
                countedChars.add(c);
                uniqueChars.remove(Character.valueOf(c));
            }
        }

        return count;
    }

    // Display guesses with their scores
    public void playerGuessScores(ArrayList<String> guesses) {
        System.out.println("Guess\tScore");
        for (String guess : guesses) {
            int letterCount = getLetterCount(guess);
            System.out.println(guess + "\t" + letterCount);
        }
    }

    // Show all words in word list
    public String showWordList() {
        StringBuilder sb = new StringBuilder("Current word list:\n");
        for (String word : wordList) {
            sb.append(word).append("\n");
        }
        return sb.toString().trim();
    }

    // Show words chosen by bot
    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played";
        }
        StringBuilder sb = new StringBuilder("Current list of played words:\n");
        for (String word : playWords) {
            sb.append(word).append("\n");
        }
        return sb.toString().trim();
    }

    // Show player's guesses and offer to update word list
    public ArrayList<String> showPlayerGuesses() {
        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
        } else {
            System.out.println("Current player guesses:");
            for (String guess : playGuesses) {
                System.out.println(guess);
            }
        }

        Scanner scan = new Scanner(System.in);
        System.out.println("Would you like to add the words to the word list? (y/n)");
        String response = scan.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            updateWordList();
            System.out.println(showWordList());
        }

        return playGuesses;
    }

    // Add a guess to player guesses
    public boolean addPlayerGuess(String wordGuess) {
        if (!playGuesses.contains(wordGuess)) {
            playGuesses.add(wordGuess);
            return true;
        }
        return false;
    }

    // Update the word list file
    public void updateWordList() {
        System.out.println("Updating word list.");

        // Add player guesses to wordList (no duplicates)
        for (String guess : playGuesses) {
            if (!wordList.contains(guess)) {
                wordList.add(guess);
            }
        }

        // Write to file
        try {
            FileWriter writer = new FileWriter(filename);
            for (String word : wordList) {
                writer.write(word + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filename);
        }
    }

    // Getters and setters
    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public ArrayList<String> getPlayedWords() {
        return playWords;
    }
}