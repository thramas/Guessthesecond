package com.pukingminion.guessthesecond;

/**
 * Created by Samarth on 28/05/17.
 */

public class DataHelper {

    public static int[] getDifficultyList() {
        return difficultyList;
    }

    private static int[] difficultyList = {40,25,15,5};
    private static int difficulty = difficultyList[1];
    public static String[] levelsListNames = {"Toddler","Child","Man","Legend"};

    static int getDifficulty() {
        return difficulty;
    }

    static void setDifficultyLevel(int difficultyLevel) {
        if(difficultyLevel >= difficultyList.length) {
            throw new ArithmeticException("You cannot set a difficulty level higher than Rajnikanth!");
        }
        DataHelper.difficulty = difficultyList[difficultyLevel];
    }


    public static String getDifficultyName(int levelOfDifficulty) {
        if(levelOfDifficulty >= difficultyList.length) {
            throw new ArithmeticException("You cannot set a difficulty level higher than Rajnikanth!");
        }
        return levelsListNames[levelOfDifficulty];
    }
}
