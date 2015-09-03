package com.hermitfang.socialdictionary.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hermitfang.socialdictionary.R;

import java.util.ArrayList;

public class PreloadWordsAdapter extends ArrayAdapter<String> {
    public PreloadWordsAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = getItem(position).toString();

        if (convertView == null) { // is recycled view ?
            // create one
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.preload_item_layout, parent, false);
        }

        TextView tvWord = (TextView) convertView.findViewById(R.id.tvWord);
        tvWord.setText(s);


        /*
        ivPhoto.setImageResource(0); // clear image first (this might be a recycled view)
        ivThumb.setImageResource(0); // as above
        Picasso.with(getContext()).load(photo.imageUrl).into(ivPhoto); // call picasso lib to load image
        Picasso.with(getContext()).load(photo.profilePicture).into(ivThumb); // call picasso lib to load image
        */

        return convertView;
    }

}
