package com.hermitfang.socialdictionary.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Categories")
public class WordlistModel extends Model {
    // This is the unique id given by the server
    @Column(name = "word_id", unique = true)
    public long wordId;
    // This is a regular field
    @Column(name = "name")
    public String name;

    // Make sure to have a default constructor for every ActiveAndroid model
    public WordlistModel(){
        super();
    }
}
