package com.sadanand.newsaggregator.loaders;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sadanand.newsaggregator.core.ShowStory;
import com.sadanand.newsaggregator.components.Story;
import com.sadanand.newsaggregator.helper.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class InvokeStory {

    private String url = Utility.API_URL;

    private ShowStory newsdisplay;
    private final String TAG = "InvokeStory";

    private String storychannels;

    private String apikey = Utility.API_KEY;
    private ArrayList<Story> story_List = new ArrayList<>();

    public InvokeStory(ShowStory newsdisplay, String storychannels) {

        this.newsdisplay = newsdisplay;
        this.storychannels = storychannels;
    }

    public void fetchSource() {

        RequestQueue queue = Volley.newRequestQueue(newsdisplay);
        String sources="sources";
        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter(sources, storychannels);
        String api_key="apiKey";
        buildURL.appendQueryParameter(api_key, apikey);
        String total_url = buildURL.build().toString();
        Log.d(TAG, "fetchSource: ");
        Response.Listener<JSONObject> Jsonlistener =
                response -> fetchDataFromResponce(response.toString());

        Response.ErrorListener error = Jsonerror -> {
            Log.d(TAG, "fetchSource: ");
            JSONObject jsonObject;
            try {
                //Log.d(TAG, "fetchSource: ");
                jsonObject = new JSONObject(new String(Jsonerror.networkResponse.data));
                Log.d(TAG, "fetchSource: " + jsonObject);
                fetchDataFromResponce(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        JsonObjectRequest jsonRequest =
                new JsonObjectRequest(Request.Method.GET, total_url,
                        null, Jsonlistener, error){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String User_agent="User-Agent";
                        Map<String, String> headers = new HashMap<>();
                        String news_app="News-App";
                        headers.put(User_agent, news_app);
                        return headers;
                    }
                };

        queue.add(jsonRequest);
    }
    private void fetchDataFromResponce(String response) {
        parseResponse(response);
        newsdisplay.adjustPublication(story_List);
    }

    private void parseResponse(String response) {
        try {
            JSONObject Obj = new JSONObject(response);
            String articles="articles";
            JSONArray jObjectJSONArray = Obj.getJSONArray(articles);
            for (int i = 0; i < jObjectJSONArray.length(); i++) {
                Story story = new Story();
                String author="author";
                story.setStoryWriter(jObjectJSONArray.getJSONObject(i).getString(author));
                String title="title";
                story.setStoryTitle(jObjectJSONArray.getJSONObject(i).getString(title));
                String url="url";
                story.setStory_site(jObjectJSONArray.getJSONObject(i).getString(url));
                String urlToImage="urlToImage";
                story.setPicture(jObjectJSONArray.getJSONObject(i).getString(urlToImage));
                String publish="publishedAt";
                story.setStory_time(jObjectJSONArray.getJSONObject(i).getString(publish));
                String desc="description";
                story.setStoryInformation(jObjectJSONArray.getJSONObject(i).getString(desc));
                story_List.add(story);
            }
        } catch (Exception exception) { exception.printStackTrace(); }
    }
}
