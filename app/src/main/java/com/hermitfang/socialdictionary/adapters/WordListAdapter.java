package com.hermitfang.socialdictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hermitfang.socialdictionary.R;

import java.util.ArrayList;

public class WordListAdapter extends ArrayAdapter<String> {

    public WordListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = getItem(position).toString();

        if (convertView == null) { // is recycled view ?
            // create one
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wordlist_item_layout, parent, false);
        }

        TextView tvWord = (TextView) convertView.findViewById(R.id.tvWordlistWord);
        tvWord.setText(s);

        return convertView;
    }

}

