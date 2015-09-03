package com.hermitfang.socialdictionary.models;

import android.app.Activity;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.query.Select;

import java.util.Iterator;
import java.util.List;

public class WordlistManager {
    protected Activity activity;

    public WordlistManager(Activity act) {
        activity = act;
    }

    public void addWord (String word) {
        List<Model> isExist = new Select()
                .from(WordlistModel.class)
                .where("name = \"" + word + "\"")
                .execute();

        if(isExist.size() <= 0) {
            java.util.Date date = new java.util.Date();
            long id = date.getTime();
            WordlistModel wordlist = new WordlistModel();
            wordlist.wordId = id;
            wordlist.name = word;
            wordlist.save();
            if (activity != null) {
                Toast.makeText(activity, word + " is added to word list", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (activity != null) {
                Toast.makeText(activity, word + " already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public List<WordlistModel> getAllWords() {
        return new Select()
                .from(WordlistModel.class)
                .where("1 = 1")
                .orderBy("name ASC")
                .execute();
    }

    public void removeWord (String word) {
        List<WordlistModel> all = getAllWords();

        int size = all.size();
        boolean cleared = false;
        for(Iterator<WordlistModel> it = all.iterator(); it.hasNext();) {
            WordlistModel m = it.next();

            if (m.name.equals(word)) {
                it.remove();
                m.delete();
                m.save();
                if (activity != null) {
                    Toast.makeText(activity, word + " is removed from word list", Toast.LENGTH_SHORT).show();
                    cleared = true;
                }
            }

            if (!it.hasNext()) {
                break;
            }
        }

        if(!cleared) {
            Toast.makeText(activity, word + " doesn't exist!", Toast.LENGTH_SHORT).show();
        }
    }
}
