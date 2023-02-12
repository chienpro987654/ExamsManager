package com.example.androidproject;

import java.util.Random;

public class RandomClass {

    String res;

    public String getRandomString(int n)
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());

            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    int getRandomInteger(int max)
    {
        Random random = new Random();
        int randomNumber = random.nextInt(max);
        return randomNumber;
    }
}
