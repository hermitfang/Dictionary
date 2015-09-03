package com.hermitfang.socialdictionary.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.models.User;
import com.hermitfang.socialdictionary.oauth.TumblrClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class SharingActivity extends BaseActivity {

    private EditText etSharingContent;
    private Bitmap screenShot;
    private ImageView ivScreenShot;
    private byte[] imageByteArray;
    private TumblrClient client;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        imageByteArray = getIntent().getByteArrayExtra("screenshot");
        if (imageByteArray == null) {
            goToGame();
            return;
        }
        screenShot = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        ivScreenShot = (ImageView) findViewById(R.id.ivScreenShot);
        ivScreenShot.setImageBitmap(screenShot);
        etSharingContent = (EditText) findViewById(R.id.etSharingContent);

        client = ((TumblrClient) TumblrClient.getInstance(TumblrClient.class, this));

        // get this user
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("DEBUG", "getUserInfo=(" + statusCode + ") " + res.toString());
                try {
                    JSONObject jsonUser = res.getJSONObject("response").getJSONObject("user");
                    user = User.fromJson(jsonUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Toast.makeText(getBaseContext(), "Found user :o", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject error) {
                Toast.makeText(getBaseContext(), "Post failed ;( Cannot get current user", Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", error.toString());
                goToGame();
            }
        });
    }

    private void goToGame() {
        Intent i = new Intent(this, GameActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    public void showGame(MenuItem item) {
        goToGame();
    }

    public void showHomeSearch(MenuItem item) {
        Intent i = new Intent(this, DictionaryActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // skip Login again
            goToGame();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sharing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doPostTwitter(View view) {
        // upload image
        client.createPhotoPost(user.getBlogHostname(), screenShot, "", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("DEBUG", "created post: " + res.toString());
                long id = 0;
                try {
                    id = res.getJSONObject("response").getLong("id");
                } catch (JSONException e) {
                    Log.d("DEBUG", "cannot parse response to json");
                    e.printStackTrace();
                    goToGame();
                }
                if (id > 0) {
                    doEditCaption(id);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getBaseContext(), "Post failed ;(", Toast.LENGTH_SHORT).show();
                goToGame();
            }
        });
    }

   public void doEditCaption(long id) {
       String content = etSharingContent.getText().toString();
       if(content == null || content.equals("")) {
           content = "Look what I have done :D";
       }
       // edit
       client.editPhotoPostCaption(user.getBlogHostname(), id, content, new JsonHttpResponseHandler() {
           @Override
           public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
               Toast.makeText(getBaseContext(), "Post Success! :D", Toast.LENGTH_SHORT).show();
               goToGame();
           }
           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               Toast.makeText(getBaseContext(), "Post failed ;(", Toast.LENGTH_SHORT).show();
               goToGame();
           }
       });
   }
}