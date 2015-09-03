package com.hermitfang.socialdictionary.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hermitfang.socialdictionary.R;
import com.hermitfang.socialdictionary.adapters.PreloadWordsAdapter;
import com.hermitfang.socialdictionary.dialogs.ConfirmAddDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DictionaryActivity extends BaseActivity {
    private EditText etQuery;
    private ArrayList<String> preloadItem;
    private PreloadWordsAdapter preloadAdapter;
    // private ArrayAdapter<String> preloadAdapter;
    private ListView lvPreload;
    private boolean ignoreEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        ignoreEnter = true;

        // setup query input
        etQuery = (EditText) findViewById(R.id.etQuery);
        etQuery.setInputType(InputType.TYPE_CLASS_TEXT);
        etQuery.requestFocus();
        etQuery.setFocusableInTouchMode(true);
        etQuery.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    String s = etQuery.getText().toString();
                    if (keyCode != KeyEvent.KEYCODE_ENTER) {
                        if (keyCode != KeyEvent.KEYCODE_DEL) {
                            String key = KeyEvent.keyCodeToString(keyCode).replace("KEYCODE_", "").toLowerCase();
                            s = s + key;
                        } else {
                            if (s.length() > 0)
                                s = s.substring(0, s.length() - 1);
                            else
                                s = "";
                        }
                        getPreloadWords(s);
                    } else {
                        if(!ignoreEnter) {
                            openDetail(etQuery.getText().toString());
                            return true;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid word \"" + s + "\"", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
            }
        });

        etQuery.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String input = etQuery.getText().toString();
                getPreloadWords(input);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // setup ListView
        lvPreload = (ListView) findViewById(R.id.lvPreload);
        preloadItem = new ArrayList<>();
        preloadAdapter = new PreloadWordsAdapter(getApplicationContext(), R.layout.preload_item_layout, preloadItem);
        // preloadAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, preloadItem);
        lvPreload.setAdapter(preloadAdapter);

        setupListViewClickListener();
        setupListViewLongClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        TextView tv = new TextView(this);
        tv.setText("");
        tv.setTextColor(getResources().getColor(R.color.abc_search_url_text));
        // tv.setOnClickListener(this);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        tv.setPadding(5, 0, 5, 0);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(14);
        menu.add(0, 0, 1, "main menu").setActionView(tv).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        getMenuInflater().inflate(R.menu.menu_dictionary, menu);
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

    // click items to preloaded words for detail (query result)
    public void setupListViewClickListener () {
        // Toast.makeText(this, "click listener", Toast.LENGTH_SHORT).show();
        lvPreload.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        String s = adapter.getItemAtPosition(pos).toString();
                        openDetail(s);
                    }
                }
        );
    }

    // long click items adding words to wordlist
    public void setupListViewLongClickListener () {
        lvPreload.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        String s = adapter.getItemAtPosition(pos).toString();
                        showConfirmDialog(s);
                        return true;
                    }
                }
        );
    }

    public void getPreloadWords(final String inputString) {
        if (inputString.equals("")) {
            if (preloadItem.size() > 0) {
                preloadItem.clear();
                preloadAdapter.notifyDataSetChanged();
            }
            return;
        }
        ignoreEnter = true;

        String url = "http://lumpedjumped.corp.sg3.yahoo.com:8666/api/auto_complete";
        // String url = "http://masterkiss.tpcity.corp.yahoo.com/api/auto_complete.php";

        RequestParams p = new RequestParams();
        p.put("p", inputString);

        /* JSON result
        {
            result: "app,appreciate,apply,application,appropriate,approach,appro,appear,appeal,apple"
        }
        */
        preloadItem.clear();
        AsyncHttpClient client = new AsyncHttpClient(8666, 443);
        client.setConnectTimeout(5000);
        client.get(url, p, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG", response.toString());

                preloadItem.clear();
                JSONArray json = null;
                try {
                    String s = response.getString("result");
                    String[] arr = s.split(",");
                    for(int i=0; i<arr.length; i++) {
                        preloadItem.add(arr[i]);
                        if(arr[i].toLowerCase().equals(inputString.toLowerCase()))
                            ignoreEnter = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                preloadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
                if(throwable.getMessage().toLowerCase().indexOf("unknownhostexception") != -1) {
                    Toast.makeText(getApplicationContext(), "API host is not reachable", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(errorResponse != null)
                    Log.i("DEBUG", errorResponse.toString());
                else
                    Log.i("DEBUG", "errorResponse is even null");
            }


        });
    }

    private void showConfirmDialog(String s) {
        FragmentManager fm = getSupportFragmentManager();
        ConfirmAddDialog confirmAddDialog = ConfirmAddDialog.newInstance(s);
        confirmAddDialog.show(fm, "fragment_edit_name");
    }

    public void clearQuery(View view) {
        etQuery.setText("");
    }
}
