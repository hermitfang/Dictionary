package com.hermitfang.socialdictionary.models;

import java.util.ArrayList;

public class PreloadModel {
    private ArrayList<String> arrWords;

    public void add(String word) {
        arrWords.add(word);
    }

    public String getWord(int position) {
        return arrWords.get(position);
    }

    public int getCount() {
        return arrWords.size();
    }
}
