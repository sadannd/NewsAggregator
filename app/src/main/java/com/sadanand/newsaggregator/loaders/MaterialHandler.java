package com.sadanand.newsaggregator.loaders;

import android.net.Uri;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sadanand.newsaggregator.core.MainActivity;
import com.sadanand.newsaggregator.components.Original_Data;
import com.sadanand.newsaggregator.helper.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MaterialHandler {
    private MainActivity mainActivity;
    private String url = Utility.SOURCE_URL;
    private final String TAG = "MaterialHandler";
    private String apikey = Utility.API_KEY;
    private ArrayList<Original_Data> info_list = new ArrayList<>();
    private ArrayList<String> bucket_list = new ArrayList<>();
    private String subject;

    public MaterialHandler(MainActivity mainactivity, String subject) {
        String emptyString="";
        this.mainActivity = mainactivity;
        String all="all";
        if (subject.isEmpty() || subject.matches(all)) { this.subject = emptyString; }
        else { this.subject = subject; }
    }

    public void retriveInfo(MainActivity mainActivity) {

        RequestQueue requestq = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        String cat="category";
        buildURL.appendQueryParameter(cat, subject);
        String key="apiKey";
        buildURL.appendQueryParameter(key, apikey);
        String finalurl = buildURL.build().toString();
        Log.d(TAG, "retriveInfo: ");
        Response.Listener<JSONObject> listener =
                response -> manageResponse(response.toString());

        Response.ErrorListener JsonErrorRes = error -> {
            JSONObject jsonObject;
            try {
                //Error
                jsonObject = new JSONObject(new String(error.networkResponse.data));
                manageResponse(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        JsonObjectRequest RequestJson =
                new JsonObjectRequest(Request.Method.GET, finalurl,
                        null, listener, JsonErrorRes){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String newsApp="News-App";
                        Map<String, String> headers = new HashMap<>();
                        String userAgent="User-Agent";
                        headers.put(userAgent, newsApp);
                        return headers;
                    }
                };
        requestq.add(RequestJson);
    }

    private void parseJSONRequest(String responseJson) {
        try {
            JSONObject jsonob = new JSONObject(responseJson);
            String sources="sources";
            JSONArray jsonobArray = jsonob.getJSONArray(sources);

            for (int i = 0; i < jsonobArray.length(); i++) {
                Original_Data og_Info = new Original_Data();
                String id="id";
                og_Info.setDataId(jsonobArray.getJSONObject(i).getString(id));
                String name="name";
                og_Info.setStory(jsonobArray.getJSONObject(i).getString(name));
                String URL="url";
                og_Info.setStory_URL(jsonobArray.getJSONObject(i).getString(URL));
                String category="category";
                og_Info.setStory_category(jsonobArray.getJSONObject(i).getString(category));
                info_list.add(og_Info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < info_list.size(); i++) {
            String news_gateway="News Gateway";
            mainActivity.setTitle(news_gateway + " (" + info_list.size() + ")");
            Log.d(TAG, "Iterating info List");
            if (!bucket_list.contains(info_list.get(i).getStory_category())) {
                bucket_list.add(info_list.get(i).getStory_category());
            }
        }
    }

    private void manageResponse(String result) {
        parseJSONRequest(result);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() { mainActivity.allocateRes(info_list, bucket_list); }
        }
        );
    }
}
