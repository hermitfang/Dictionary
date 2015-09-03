package com.hermitfang.socialdictionary.activities;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.dialogs.ShareToTumblrDialog;
import com.hermitfang.socialdictionary.models.DetailModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;


public class GameActivity extends BaseActivity implements ShareToTumblrDialog.NoticeDialogListener {

    String[] letter = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    int[] ResourceTv = {
            R.id.tvA, R.id.tvB, R.id.tvC, R.id.tvD, R.id.tvE, R.id.tvF, R.id.tvG,
            R.id.tvH, R.id.tvI, R.id.tvJ, R.id.tvK, R.id.tvL, R.id.tvM, R.id.tvN,
            R.id.tvO, R.id.tvP, R.id.tvQ, R.id.tvR, R.id.tvS, R.id.tvT, R.id.tvU,
            R.id.tvV, R.id.tvW, R.id.tvX, R.id.tvY, R.id.tvZ
    };
    int[] ResourceTvResult = {
            R.id.tvResult1, R.id.tvResult2, R.id.tvResult3, R.id.tvResult4,
            R.id.tvResult5, R.id.tvResult6, R.id.tvResult7, R.id.tvResult8,
            R.id.tvResult9
    };
    int[] ResourceTvResult_2 = {
            R.id.tvResult41, R.id.tvResult42, R.id.tvResult43, R.id.tvResult44,
            R.id.tvResult4, R.id.tvResult45, R.id.tvResult46, R.id.tvResult47,
            R.id.tvResult48
    };
    int[] ResourceTvResult_3 = {
            R.id.tvResult71, R.id.tvResult72, R.id.tvResult73, R.id.tvResult74,
            R.id.tvResult7, R.id.tvResult75, R.id.tvResult76, R.id.tvResult77,
            R.id.tvResult78
    };
    TextView[] tv = new TextView[26];
    int[] showChar = {1, 4, 7};

    TextView[] tvResult = new TextView[9];
    String[] question;
    int start;
    boolean soundPlayed;
    TextView tvHint;

    TextView[] tvResult_2 = new TextView[9];
    String[] question_2;
    int start_2;
    boolean soundPlayed2;
    TextView tvHint2;

    TextView[] tvResult_3 = new TextView[9];
    String[] question_3;
    int start_3;
    boolean soundPlayed3;
    TextView tvHint3;

    String c;

    WordList wordList = new WordList();
    RelativeLayout layout;
    MediaPlayer mediaPlayer;

    // screen shot variables
    private Bitmap screenShot;
    private byte[] screenShotBytes;
    final private static int COMPRESSION_FACTOR = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        for (int i=0; i < letter.length;i++) {
            tv[i] = (TextView) findViewById(ResourceTv[i]);
            tv[i].setTag(R.id.view_tag, letter[i]);
            tv[i].setOnTouchListener(new MyTouchListener());
        }

        mediaPlayer = new MediaPlayer();
        load_question();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    public void load_question() {
        question = wordList.getOne();
        tvHint = (TextView) findViewById(R.id.tvHint);
        tvHint.setText(question[1]);

        soundPlayed = false;
        soundPlayed2 = false;
        soundPlayed3 = false;

        start = (ResourceTvResult.length - question[0].length() ) / 2 ;

        for ( int i=0; i < ResourceTvResult.length; i++ ) {
            tvResult[i] = (TextView) findViewById(ResourceTvResult[i]);
            if ( i >= start && i < start+question[0].length() ) {
                if (contains(showChar, i)) {
                    c = String.valueOf(question[0].charAt(i - start));
                    tvResult[i].setText(c);
                } else {
                    tvResult[i].setText("");
                }
                tvResult[i].setVisibility(View.VISIBLE);
                tvResult[i].setOnDragListener(new MyDragListener());
                tvResult[i].setBackgroundResource(R.mipmap.letter_question);
            } else {
                tvResult[i].setVisibility(View.INVISIBLE);
                tvResult[i].setText("");
            }
        }

        tvHint2 = (TextView) findViewById(R.id.tvHint2);
        tvHint2.setText("");
        for ( int i=0; i < ResourceTvResult_2.length; i++ ) {
            if ( i != 4 ) {
                tvResult_2[i] = (TextView) findViewById(ResourceTvResult_2[i]);
                tvResult_2[i].setVisibility(View.INVISIBLE);
                tvResult_2[i].setText("");
            }
        }

        if ( start + question[0].length() > 4 ) {
            question_2 = wordList.getOne(question, 4, question[0].charAt(3-start) );

            if ( !question_2[0].isEmpty() ) {
                tvHint2 = (TextView) findViewById(R.id.tvHint2);
                tvHint2.setText(question_2[1]);
                start_2 = (ResourceTvResult_2.length - question_2[0].length() ) / 2 ;

                for ( int i=0; i < ResourceTvResult_2.length; i++ ) {
                    tvResult_2[i] = (TextView) findViewById(ResourceTvResult_2[i]);

                    if (i >= start_2 && i < start_2 + question_2[0].length()) {
                        tvResult_2[i].setVisibility(View.VISIBLE);
                        // if (i==4) {
                        if (contains(showChar, i)) {
                            c = String.valueOf( question_2[0].charAt(i-start_2) );
                            tvResult_2[i].setText(c);
                        } else {
                            tvResult_2[i].setText("");
                        }
                        tvResult_2[i].setOnDragListener(new MyDragListener());
                        tvResult_2[i].setBackgroundResource(R.mipmap.letter_question);
                    } else {
                        tvResult_2[i].setVisibility(View.INVISIBLE);
                        tvResult_2[i].setText("");
                    }
                }
            }
        }

        tvHint3 = (TextView) findViewById(R.id.tvHint3);
        tvHint3.setText("");
        for ( int i=0; i < ResourceTvResult_3.length; i++ ) {
            if ( i != 4 ) {
                tvResult_3[i] = (TextView) findViewById(ResourceTvResult_3[i]);
                tvResult_3[i].setVisibility(View.INVISIBLE);
                tvResult_3[i].setText("");
            }
        }

        if ( start + question[0].length() >= 7 ) {
            question_3 = wordList.getOne(question, 4, question[0].charAt(6-start) );

            if ( !question_3[0].isEmpty() ) {
                if (question_2[0].isEmpty()) {
                    tvHint2 = (TextView) findViewById(R.id.tvHint2);
                    tvHint2.setText(question_3[1]);
                    tvHint3 = (TextView) findViewById(R.id.tvHint3);
                    tvHint3.setText("");
                } else {
                    tvHint3 = (TextView) findViewById(R.id.tvHint3);
                    tvHint3.setText(question_3[1]);
                }
                start_3 = (ResourceTvResult_3.length - question_3[0].length() ) / 2 ;

                for ( int i=0; i < ResourceTvResult_3.length; i++ ) {
                    tvResult_3[i] = (TextView) findViewById(ResourceTvResult_3[i]);

                    if (i >= start_3 && i < start_3 + question_3[0].length()) {
                        tvResult_3[i].setVisibility(View.VISIBLE);
                        if (contains(showChar, i)) {
                            c = String.valueOf( question_3[0].charAt(i-start_3) );
                            tvResult_3[i].setText(c);
                        } else {
                            tvResult_3[i].setText("");
                        }
                        tvResult_3[i].setOnDragListener(new MyDragListener());
                        tvResult_3[i].setBackgroundResource(R.mipmap.letter_question);
                    } else {
                        tvResult_3[i].setVisibility(View.INVISIBLE);
                        tvResult_3[i].setText("");
                    }
                }
            }
        }
    }

    public static boolean contains(final int[] array, final int v) {
        for (final int e : array)
            if (e == v )
                return true;
        return false;
    }

    // @Override
    // public void callOKFunc() {
    //     doScreenShot();
    // }

    // This defines your touch listener
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData.Item item = new ClipData.Item((CharSequence)view.getTag(R.id.view_tag));

                String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
                ClipData data = new ClipData(view.getTag(R.id.view_tag).toString(), mimeTypes, item);

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                // view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Log.d("DEBUG", "ACTION DRAG ENTERED" );
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    // Log.d("DEBUG", "ACTION DRAP EXITED" );
                    break;
                case DragEvent.ACTION_DROP:
                    // Log.d("DEBUG", "ACTION DROP" );
                    View view = (View) event.getLocalState();
                    ViewGroup viewgroup = (ViewGroup) view.getParent();
                    String tag = (String)view.getTag(R.id.view_tag);

                    ((TextView) v).setText(tag);
                    v.setBackgroundResource(R.mipmap.letter_purple);

                    checkResult();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Log.d("DEBUG", "ACTION DRAG ENDED");
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    public void checkResult() {
        String result = "";
        String result2= "";
        String result3= "";

        for (TextView aTvResult : tvResult) {
            result += (aTvResult.getText().toString());
        }

        if ( !question_2[0].isEmpty() ) {
            for (TextView aTvResult_2 : tvResult_2) {
                result2 += (aTvResult_2.getText().toString());
            }
        }

        if ( !question_3[0].isEmpty() ) {
            for (TextView aTvResult_3 : tvResult_3) {
                result3 += (aTvResult_3.getText().toString());
            }
        }

        if (!soundPlayed && question[0].equalsIgnoreCase(result)) {
            Toast.makeText(this, "Bingo: " + result.toLowerCase(), Toast.LENGTH_SHORT).show();
            playSound(result);
            soundPlayed = true;
        }

        if (!soundPlayed2 && question_2[0].equalsIgnoreCase(result2)){
            Toast.makeText(this, "Bingo: " + result2.toLowerCase(), Toast.LENGTH_SHORT).show();
            playSound(result2);
            soundPlayed2 = true;
        }

        if (!soundPlayed3 && question_3[0].equalsIgnoreCase(result3)) {
            Toast.makeText(this, "Bingo: " + result3.toLowerCase(), Toast.LENGTH_SHORT).show();
            playSound(result3);
            soundPlayed3 = true;
        }

        //if ( question[0].equalsIgnoreCase(result) && question_2[0].equalsIgnoreCase(result2) && question_3[0].equalsIgnoreCase(result3) ) {
        if (soundPlayed && soundPlayed2 && soundPlayed3) {
            Toast.makeText(this, "Bingo, you got the right answer.", Toast.LENGTH_SHORT).show();
            showShareDialog("success");
        }
    }

    // for dialog
    private void showShareDialog(String flag) {
        FragmentManager fm = getSupportFragmentManager();
        ShareToTumblrDialog shareToTumblrDialog = ShareToTumblrDialog.newInstance(flag);
        shareToTumblrDialog.show(fm, "fragment_share_name");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        // Toast.makeText(this, "click ok", Toast.LENGTH_SHORT).show();
        doScreenShot();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        // Toast.makeText(this, "click cancel", Toast.LENGTH_SHORT).show();
    }

    public void getAnswer( View v ) {
        showShareDialog("share");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
        // getMenuInflater().inflate(R.menu.menu_game, menu);
        // return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload_question) {
            load_question();
            return true;
        }

        if (id == R.id.action_game_share) {
            doScreenShot();
        }

        return super.onOptionsItemSelected(item);
    }

    /* screen shot functions */
    public void doScreenShot() {
        screenShot = getScreenShot();
        // to bytes
        screenShotBytes = bitmap2Bytes(screenShot);
        // start sharing activity
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("screenshot", screenShotBytes);
        startActivity(i);
    }

    private byte[] bitmap2Bytes(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, COMPRESSION_FACTOR, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private Bitmap getScreenShot()
    {
        // cache screenshot by View and put it in Bitmap
        View mView = getWindow().getDecorView();
        mView.setDrawingCacheEnabled(true);
        mView.buildDrawingCache();
        Bitmap mFullBitmap = mView.getDrawingCache();

        // get height of statusbar
        Rect mRect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
        int mStatusBarHeight = mRect.top;

        // get H/W of phone screen
        int mPhoneWidth = getWindowManager().getDefaultDisplay().getWidth();
        int mPhoneHeight = getWindowManager().getDefaultDisplay().getHeight();

        Log.d("DEBUG", "W(" + mPhoneWidth + ") H(" + mPhoneHeight + ") Bar(" + mStatusBarHeight + ")");

        // corp (x,y) and H/W
        long x = 0;
        long y = mStatusBarHeight;
        long h = mPhoneHeight - mStatusBarHeight - 345;
        long w = mPhoneWidth;
        Matrix matrix = new Matrix();
        matrix.postScale((float) 0.3, (float) 0.3);
        Log.d("DEBUG", "(x,y) = (" + x + "," + y + ") and H/W = " + h + "/" + w);

        // remove actionbar from image(Bitmap) and make a new one
        Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, (int) x, (int) y, (int) w, (int) h, matrix, true);

        //clear cache
        mView.destroyDrawingCache();

        return mBitmap;
    }

    public void showHomeSearch(MenuItem item) {
        Intent i = new Intent(this, DictionaryActivity.class);
        startActivity(i);
    }

    public void playSound(String input) {
        String inputString = input.toLowerCase();
        String url = "http://lumpedjumped.corp.sg3.yahoo.com:8666/api/query";
        RequestParams p = new RequestParams();
        p.put("p", inputString);

        AsyncHttpClient client = new AsyncHttpClient(8666, 443);
        client.get(url, p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG", response.toString());
                DetailModel m = new DetailModel(response);

                String s = m.getExample();
                if (!s.equals("")) {
                    String mp3 = m.getSoundUrl();
                    if (!mp3.equals("")) {
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(mp3);
                            mediaPlayer.prepareAsync();
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                        } catch (Exception e) {
                            Log.i("DEBUG", "play sound " + mp3 + " error");
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (throwable.getMessage().toLowerCase().indexOf("unknownhostexception") != -1) {
                    Log.i("DEBUG", "API host is not reachable");
                    return;
                }
                if (errorResponse != null)
                    Log.i("DEBUG", errorResponse.toString());
                else
                    Log.i("DEBUG", "error is even null");
            }
        });
    }
}

class WordList {
    ArrayList<String[]> wordList;

    public WordList() {
        wordList = new ArrayList<>();
        wordList.add(new String[]{"abandon", "[動詞] 放棄; [名詞] 放縱;放任"});
        wordList.add(new String[]{"abbreviate", "[動詞] 縮寫"});
        wordList.add(new String[]{"abnormal", "[形容詞] 不正常的"});
        wordList.add(new String[]{"abolish", "[動詞] 廢除;廢止"});
        wordList.add(new String[]{"absence", "[名詞] 缺席"});
        wordList.add(new String[]{"abstraction", "[名詞] 抽象"});
        wordList.add(new String[]{"academy", "[名詞] 學院;大學;研究院"});
        wordList.add(new String[]{"accomplished", "[形容詞] 完成了的;有造詣的"});
        wordList.add(new String[]{"accurate", "[形容詞] 準確的"});
        wordList.add(new String[]{"achieve", "[動詞] 實現"});
        wordList.add(new String[]{"activity", "[名詞] 活動"});
        wordList.add(new String[]{"adjustment", "[名詞] 調節;調整"});
        wordList.add(new String[]{"admiration", "[名詞] 欽佩;羨慕"});
        wordList.add(new String[]{"advantage", "[名詞] 優點,優勢;利益"});
        wordList.add(new String[]{"advance", "[動詞] 推進;提升;貸(款); [名詞] 前進;發展;預付"});
        wordList.add(new String[]{"advice", "[名詞] 忠告"});
        wordList.add(new String[]{"afternoon", "[名詞] 下午;午後"});
        wordList.add(new String[]{"against", "[介系詞] 反對"});
        wordList.add(new String[]{"amazing", "[形容詞] 驚人的,令人吃驚的"});
        wordList.add(new String[]{"ambiguity", "[名詞] 意義不明確;模稜兩可的話"});
        wordList.add(new String[]{"babysit", "[動詞] 當保姆"});
        wordList.add(new String[]{"backbone", "[名詞] 脊柱;支柱;骨幹"});
        wordList.add(new String[]{"background", "[名詞] 背景"});
        wordList.add(new String[]{"balcony", "[名詞] 陽臺"});
        wordList.add(new String[]{"barbarian", "[形容詞] 野蠻(人)的; [名詞] 野蠻人"});
        wordList.add(new String[]{"barbershop", "[名詞] 理髮店"});
        wordList.add(new String[]{"batch", "[名詞] 一批;一群;一爐;一團"});
        wordList.add(new String[]{"beautiful", "[形容詞] 漂亮的"});
        wordList.add(new String[]{"badminton", "[名詞] 羽毛球"});
        wordList.add(new String[]{"benefit", "[動詞] 對...有益; [名詞] 利益;好處"});
        wordList.add(new String[]{"blizzard", "[名詞] 大風雪"});
        wordList.add(new String[]{"blouse", "[名詞] 女裝短上衣"});
        wordList.add(new String[]{"bracket", "[動詞] 用托架固定;用括弧括;相提並論;"});
        wordList.add(new String[]{"brochure", "[名詞] 小冊子"});
        wordList.add(new String[]{"butcher", "[動詞] 屠宰(牲口); [名詞] 肉販"});
        wordList.add(new String[]{"cabinet", "[名詞] 櫥,櫃;內閣"});
        wordList.add(new String[]{"caffeine", "[名詞] 咖啡因"});
        wordList.add(new String[]{"calculate", "[動詞] 計算"});
        wordList.add(new String[]{"campaign", "[動詞] 參加競選; [名詞] 運動,活動"});
        wordList.add(new String[]{"campus", "[名詞] 校園"});
        wordList.add(new String[]{"capability", "[名詞] 能力"});
        wordList.add(new String[]{"capsule", "[名詞] 小盒;膠囊;太空艙"});
        wordList.add(new String[]{"carrier", "[名詞] 運送人;從事運輸業公司;帶菌者"});
        wordList.add(new String[]{"cashier", "[動詞] 開除;丟棄; [名詞] 出納員"});
        wordList.add(new String[]{"casino", "[名詞] 賭場"});
        wordList.add(new String[]{"challenge", "[動詞] 對...提出異議; [名詞] 挑戰;艱鉅的事"});
        wordList.add(new String[]{"champagne", "[名詞] 香檳酒;香檳酒色"});
        wordList.add(new String[]{"charge", "[動詞] 索價;充電; [名詞] 費用;充電"});
        wordList.add(new String[]{"charming", "[形容詞] 迷人的;有魅力的"});
        wordList.add(new String[]{"circuit", "[名詞] 環道;一圈;巡迴路線;電路"});
        wordList.add(new String[]{"clarify", "[動詞] 澄清;闡明"});
        wordList.add(new String[]{"clone", "[動詞] 複製; [名詞] 無性繁殖系;複製品"});
        wordList.add(new String[]{"coherent", "[形容詞] 一致的;條理清楚的;連貫的"});
        wordList.add(new String[]{"collision", "[名詞] 相撞;衝突;抵觸"});
        wordList.add(new String[]{"combat", "[動詞] 戰鬥;搏鬥;反對; [名詞] 戰鬥;格鬥;反對"});
        wordList.add(new String[]{"comment", "[動詞] 評論; [名詞] 評論"});
        wordList.add(new String[]{"commute", "[動詞] 減刑;通勤; [名詞] 通勤"});
        wordList.add(new String[]{"concern", "[動詞] 使擔心; [名詞] 關懷"});
        wordList.add(new String[]{"concert", "[名詞] 音樂會,演奏會"});
        wordList.add(new String[]{"confuse", "[動詞] 使迷惑"});
        wordList.add(new String[]{"conquer", "[動詞] 克服;征服"});
        wordList.add(new String[]{"consult", "[動詞] 與...商量"});
        wordList.add(new String[]{"continue", "[動詞] 繼續"});
        wordList.add(new String[]{"control", "[動詞] 控制; [名詞] 支配;"});
        wordList.add(new String[]{"convince", "[動詞] 使信服;說服"});
        wordList.add(new String[]{"corrupt", "[形容詞] 腐敗的; [動詞] 使腐敗;賄賂"});
        wordList.add(new String[]{"cousin", "[名詞] 表(堂)兄弟姊妹"});
        wordList.add(new String[]{"crazy", "[形容詞] 古怪的;著迷的"});
        wordList.add(new String[]{"crumb", "[名詞] 麵包屑;碎屑;少許"});
        wordList.add(new String[]{"cuisine", "[名詞] 烹飪(法);菜餚"});
        wordList.add(new String[]{"cylinder", "[名詞] 圓柱;圓筒"});
        wordList.add(new String[]{"kindle", "[動詞] 點燃;煽動;照亮"});
        wordList.add(new String[]{"kitchen", "[名詞] 廚房"});
        wordList.add(new String[]{"knife", "[名詞] 刀子"});
    }

    public WordList(ArrayList<String[]> wordList) {
        this.wordList = wordList;
    }

    public ArrayList<String[]> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<String[]> wordList) {
        this.wordList = wordList;
    }

    public String[] getOne() {
        Random rand = new Random();
        String[] retVal;
        int n;

        do {
            n = rand.nextInt(wordList.size());
            retVal = wordList.get(n);
        } while (retVal[0].length() >= 9);

        return retVal;
    }

    public String[] getOne(String[] question, int pos, char c ) {
        Random rand = new Random();
        String[] retVal;
        int index = rand.nextInt(9);
        int n;
        int start;
        char result_c;
        int count = 0;

        // Log.d("DEBUG", "char:" + c);

        do {
            n = rand.nextInt(wordList.size());
            retVal = wordList.get(n);
            if (retVal[0].length() <= 9 && !retVal[0].equals(question[0])) {
                start = (9 - retVal[0].length()) / 2;
                result_c = retVal[0].charAt(pos-start);
            } else {
                result_c = '0';
            }
            count++;
            if ( count > 200 ) {
                retVal = new String[] {"",""};
                break;
            }
        } while ( result_c != c );

        return retVal;
    }
}
