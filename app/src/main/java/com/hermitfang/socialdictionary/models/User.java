package com.hermitfang.socialdictionary.models;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
    private static User currentUser;
    protected JSONObject jsonObject;

    public String getBlogHostname() {
        try {
            JSONArray blogs = jsonObject.getJSONArray("blogs");
            JSONObject blog = (JSONObject)blogs.get(0);
            return blog.getString("name") + ".tumblr.com";
        } catch (Exception e) {
            return null;
        }
    }

    public static User fromJson(JSONObject jsonObject) {
        User user = new User();
        user.jsonObject = jsonObject;
        return user;
    }
}