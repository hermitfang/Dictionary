package com.hermitfang.socialdictionary.models;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailModel {
    private String pronunciationKK;
    private String pronunciationDJ;
    private String posName;
    private String translation;
    private String example;
    private String exampleTrans;
    private String soundUrl;

    public String getPronunciationKK() {
        return pronunciationKK;
    }

    public String getPronunciationDJ() {
        return pronunciationDJ;
    }

    public String getPosName() {
        return posName;
    }

    public String getTranslation() {
        return translation;
    }

    public String getExample() {
        return example;
    }

    public String getExampleTrans() {
        return exampleTrans;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public DetailModel(JSONObject data) {
        JSONObject json = null;
        try {
            json = data.getJSONObject("terms");
            try {
                pronunciationKK = json.getJSONObject("mainTerm").getJSONArray("pronunciation").getJSONObject(0).getString("value");
                pronunciationDJ = json.getJSONObject("mainTerm").getJSONArray("pronunciation").getJSONObject(1).getString("value");
            } catch (JSONException e) {
                pronunciationKK = "";
                pronunciationDJ = "";
            }

            JSONObject jsonMain = json.getJSONObject("mainTerm").getJSONObject("definition").getJSONArray("example").getJSONObject(0);
            posName = jsonMain.getString("posName");
            translation = jsonMain.getJSONArray("explanations").getJSONObject(0).getString("explanation");
            // example = jsonMain.getJSONArray("explanations").getJSONObject(0).getJSONArray("examples").getJSONObject(0).getString("exampleHTML");
            example = jsonMain.getJSONArray("explanations").getJSONObject(0).getJSONArray("examples").getJSONObject(0).getString("example");
            exampleTrans = jsonMain.getJSONArray("explanations").getJSONObject(0).getJSONArray("examples").getJSONObject(0).getString("translation");

            try {
                soundUrl = json.getJSONObject("mainTerm").getJSONArray("sounds").getJSONObject(0).getString("mp3");
            }
            catch (JSONException e) {
                soundUrl = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            pronunciationKK = "";
            pronunciationDJ = "";
            posName = "";
            translation = "";
            example = "";
            exampleTrans = "";
            soundUrl = "";
        }
    }
}
