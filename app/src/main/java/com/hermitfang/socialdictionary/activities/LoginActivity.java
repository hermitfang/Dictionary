package com.hermitfang.socialdictionary.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.oauth.TumblrClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TumblrClient> {

    private byte[] screenShotBytes;

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
        setContentView(R.layout.activity_login);
        screenShotBytes = getIntent().getByteArrayExtra("screenshot");
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "Connected to Tumblr!", Toast.LENGTH_SHORT).show();
        // start sharing activity
        Intent i = new Intent(this, SharingActivity.class);
        i.putExtra("screenshot", screenShotBytes);
        startActivity(i);
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        Toast.makeText(this, "Login Failed :(", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
        LoginActivity.this.finish();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }

}
