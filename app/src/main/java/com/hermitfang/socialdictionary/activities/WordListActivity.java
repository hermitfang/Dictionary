package com.hermitfang.socialdictionary.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.adapters.WordListAdapter;
import com.hermitfang.socialdictionary.dialogs.ConfirmRemoveDialog;
import com.hermitfang.socialdictionary.models.WordlistManager;
import com.hermitfang.socialdictionary.models.WordlistModel;

import java.util.ArrayList;
import java.util.List;


public class WordListActivity extends BaseActivity {
    private ListView lvWordlist;
    private ArrayList<String> myWordlist;
    // private ArrayAdapter<String> myWordlistAdapter;
    private WordListAdapter myWordlistAdapter;
    WordlistManager wordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);

        wordManager = new WordlistManager(this);
        myWordlist = new ArrayList<>();

        List<WordlistModel> a = wordManager.getAllWords();
        if(a.size() > 0) {
            for (int i = 0; i < a.size(); i++) {
                myWordlist.add(a.get(i).name);
            }
        }
        else {
            Toast.makeText(this, "Word list is currently emtpy", Toast.LENGTH_SHORT).show();
        }

        //myWordlistAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myWordlist);
        myWordlistAdapter = new WordListAdapter(this, R.layout.wordlist_item_layout, myWordlist);

        lvWordlist = (ListView)findViewById(R.id.lvWordlist);
        lvWordlist.setAdapter(myWordlistAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //     return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    public void showDetail(View v) {
        TextView t = (TextView)v.findViewById(R.id.tvWordlistWord);
        if(t != null) {
            String s = t.getText().toString();
            openDetail(s);
        }
    }

    public void confirmRemoveWord(View v) {
        View parentView = (View)v.getParent();
        TextView t = (TextView)parentView.findViewById(R.id.tvWordlistWord);
        if (t != null) {
            String s = t.getText().toString();
            showRemoveDialog(s);
        }
    }

    public void removeWord(String s) {
        WordlistManager m = new WordlistManager(this);
        m.removeWord(s);
        List<WordlistModel> all = m.getAllWords();

        myWordlist.clear();
        for(int i=0; i<all.size(); i++) {
            myWordlist.add(all.get(i).name.toString());
        }
        myWordlistAdapter.notifyDataSetChanged();
    }

    private void showRemoveDialog(String s) {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmRemoveDialog confirmRemoveDialog = ConfirmRemoveDialog.newInstance(s, this);
        confirmRemoveDialog.show(fm, "fragment_edit_name");
    }
}
