package com.hermitfang.socialdictionary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            Color c = new Color();
            int code = c.parseColor("#ff34d9ff");
            ColorDrawable cd = new ColorDrawable(code);
            bar.setBackgroundDrawable(cd);
        }
        else {
            Toast.makeText(this, "bar is null", Toast.LENGTH_SHORT).show();
        }
    }

    protected void openDetail (String s) {
        // Toast.makeText(getApplicationContext(), "show translation '" + s + "'", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("etQuery", s);
        startActivity(i);
    }

    /*
    protected void openWordList () {
        // Toast.makeText(this, "go to word list", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, WordListActivity.class);
        startActivity(i);
    }

    protected void openGame () {
        // Toast.makeText(this, "go to game", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
    */

    public void showWordList(MenuItem item) {
        // Toast.makeText(this, "go to word list", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, WordListActivity.class);
        startActivity(i);
    }

    public void showGame(MenuItem item) {
        // Toast.makeText(this, "go to game", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    public void showDictionaryQuery(MenuItem item) {
        Intent i = new Intent(this, DictionaryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }
}
