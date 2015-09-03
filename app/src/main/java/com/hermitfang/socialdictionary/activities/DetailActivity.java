package com.hermitfang.socialdictionary.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.models.DetailModel;
import com.hermitfang.socialdictionary.models.WordlistManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;


public class DetailActivity extends BaseActivity {
    private String query;
    private boolean isValidWord;
    private TextView tvTitle;
    private TextView tvPronunciation;
    private ImageButton ibPlaySound;
    private TextView tvPosName;
    private TextView tvTranslation;
    private TextView tvExampleTitle;
    private TextView tvExample;
    private TextView tvExampleTranslation;
    private ProgressBar prgRolling;
    private Layout loInfo;
    private String soundUrl;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        query = i.getStringExtra("etQuery");
        isValidWord = false;
        soundUrl = "";

        mediaPlayer = new MediaPlayer();

        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvPronunciation = (TextView)findViewById(R.id.tvPronunciation);
        ibPlaySound = (ImageButton)findViewById(R.id.ibPlaySound);
        tvPosName = (TextView)findViewById(R.id.tvPosName);
        tvTranslation = (TextView)findViewById(R.id.tvTranslation);
        tvExampleTitle = (TextView)findViewById(R.id.tvExampleTitle);
        tvExample = (TextView)findViewById(R.id.tvExample);
        tvExampleTranslation = (TextView)findViewById(R.id.tvExampleTranslation);
        prgRolling = (ProgressBar)findViewById(R.id.prgRolling);

        tvTitle.setText(query);

        ibPlaySound.setVisibility(View.INVISIBLE);
        /*
        // calling API and to simulate network latency
        prgRolling.setVisibility(View.VISIBLE);
        int latency = new Random().nextInt((1000 - 500) + 1) + 500;
        Timer latencyTimer = new Timer();
        latencyTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        getTranslation(query);
                    }
                });
            }
        }, latency);
        */
        getTranslation(query);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    private void getTranslation(String inputString) {
        isValidWord = false;
        String url = "http://lumpedjumped.corp.sg3.yahoo.com:8666/api/query";

        RequestParams p = new RequestParams();
        p.put("p", inputString);
        Log.i("DEBUG DETAIL", inputString);

        AsyncHttpClient client = new AsyncHttpClient(8666, 443);
        client.get(url, p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG", response.toString());
                DetailModel m = new DetailModel(response);

                tvPronunciation.setText("KK[" + m.getPronunciationKK() + "] DJ[" + m.getPronunciationDJ() + "]");
                ibPlaySound.setVisibility(View.VISIBLE);
                tvPosName.setText(m.getPosName());
                tvTranslation.setText(m.getTranslation());
                soundUrl = m.getSoundUrl();

                String s = m.getExample();
                if (!s.equals("")) {
                    s = s.replaceAll(query, "<font color='blue'>" + query + "</font>");
                    tvExampleTitle.setText("Example");
                    tvExample.setText(Html.fromHtml(s));
                    tvExampleTranslation.setText(m.getExampleTrans());
                    isValidWord = true;
                }
                else {
                    tvExampleTitle.setText("");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
                isValidWord = false;
                ibPlaySound.setVisibility(View.INVISIBLE);
                if(throwable.getMessage().toLowerCase().indexOf("unknownhostexception") != -1) {
                    Toast.makeText(getApplicationContext(), "API host is not reachable", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(errorResponse != null)
                    Log.i("DEBUG", errorResponse.toString());
                else
                    Log.i("DEBUG", "error is even null");
            }

            @Override
            public void onStart() {
                super.onStart();
                prgRolling.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                prgRolling.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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

    public void addToWordList(MenuItem item) {
        if(!query.equals("")) {
            if(isValidWord) {
                WordlistManager m = new WordlistManager(this);
                m.addWord(tvTitle.getText().toString());
            }
            else {
                Toast.makeText(this, "\"" + query + "\" is not found in dictionary", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void playSound (View v) {
        if(!soundUrl.equals("")) {
            try {
                prgRolling.setVisibility(View.VISIBLE);
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(soundUrl);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        prgRolling.setVisibility(View.INVISIBLE);
                        mp.start();
                    }
                });
            } catch (Exception e) {
                Log.i("DEBUG", "play sound " + soundUrl + " error");
            }
        }
    }
}
