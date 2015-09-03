package com.hermitfang.socialdictionary.networks;

import android.app.Activity;
import android.util.Log;

import com.hermitfang.socialdictionary.models.DetailModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class GetTranslation {
    private String queryWord;
    private boolean isValidWord;
    private DetailModel detail;
    private Activity parentActivity;
    private static final String url = "http://lumpedjumped.corp.sg3.yahoo.com:8666/api/query";

    public GetTranslation(String inputString, Activity parent) {
        queryWord = inputString;
        isValidWord = false;
        parentActivity = parent;
        getTranslation(inputString);
    }

    private void getTranslation(String inputString) {
        isValidWord = false;

        RequestParams p = new RequestParams();
        p.put("p", inputString);
        Log.i("DEBUG DETAIL", inputString);

        AsyncHttpClient client = new AsyncHttpClient(8666, 443);
        client.get(url, p, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("DEBUG", response.toString());
                DetailModel m = new DetailModel(response);

                String s = m.getExample();
                if (!s.equals("")) {
                    detail = m;
                    isValidWord = true;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // super.onFailure(statusCode, headers, throwable, errorResponse);
                isValidWord = false;
                if(throwable.getMessage().toLowerCase().indexOf("unknownhostexception") != -1) {
                    Log.i("DEBUG", "API host is not reachable");
                    return;
                }
                if(errorResponse != null)
                    Log.i("DEBUG", errorResponse.toString());
                else
                    Log.i("DEBUG", "error is even null");
            }
        });
    }
}
