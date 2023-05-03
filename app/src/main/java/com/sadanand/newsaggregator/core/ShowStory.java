package com.sadanand.newsaggregator.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.sadanand.newsaggregator.components.Original_Data;
import com.sadanand.newsaggregator.components.Story;
import com.sadanand.newsaggregator.helper.Utility;
import com.sadanand.newsaggregator.loaders.InvokeStory;

import java.io.Serializable;
import java.util.ArrayList;
public class ShowStory extends Service {
    private ArrayList<Story> storyArray = new ArrayList<>();

    private ResponseReceiver response;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        response = new ResponseReceiver();
        registerReceiver(response, new IntentFilter(Utility.SERVICE_ACTION));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            destroy();
            super.onDestroy();
        }catch(IllegalArgumentException e){ }
    }

    private void destroy() {
        stopSelf();
        unregisterReceiver(response);
    }

    public void adjustPublication(ArrayList<Story> arrayList) {
        storyArray.clear();
        storyArray = new ArrayList<>(arrayList);
        Intent newIntent = new Intent(Utility.STORY_NEWS);
        Log.d("ShowStory", "adjustPublication: ");

        newIntent.putExtra(Utility.STORY_DATA_ARTICLE, (Serializable) storyArray);
        sendBroadcast(newIntent);
        storyArray.clear();
    }

    public class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Utility.SERVICE_ACTION) {
                    if (intent.hasExtra(Utility.SOURCE_INFO)) {
                        Original_Data story_src = (Original_Data) intent.getSerializableExtra(Utility.SOURCE_INFO);
                        InvokeStory invokeStory = new InvokeStory(ShowStory.this, "" + story_src.getDataId());
                        invokeStory.fetchSource();
                    }
            } }
    }}