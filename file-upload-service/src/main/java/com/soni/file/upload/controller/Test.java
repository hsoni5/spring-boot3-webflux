package com.soni.file.upload.controller;

public class Test {
    public static void main(String[] args) {
        String str = "karnatka";
        char[] charArray = str.toCharArray();
        String result = "";
        char temp = 0;
        for (int i = 0; i <= charArray.length-1; i++) {
            for (int j = 1; j <= charArray.length-1; j++) {
                if (charArray[i] != charArray[j]) {
                    temp = charArray[i];
                    charArray[i] = charArray[j];
                    charArray[i] = temp;
                }
            }
        }
        System.out.println(result+temp);
    }
}
