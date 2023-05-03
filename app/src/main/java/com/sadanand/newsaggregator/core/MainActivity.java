package com.sadanand.newsaggregator.core;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pjnaik.newsaggregator.R;
import com.sadanand.newsaggregator.components.Original_Data;
import com.sadanand.newsaggregator.components.Story;
import com.sadanand.newsaggregator.helper.Utility;
import com.sadanand.newsaggregator.loaders.MaterialHandler;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Menu menus;

    private DrawerLayout drawer_layout;
    private ListView listviewItems;
    private ArrayList<String> textlist = new ArrayList<>();
    private ArrayList<Story> story_Array_List = new ArrayList<>();
    private ViewPageAdapter pageadapter;

    private EnterDetails enterDetails;
    private ArrayList<String> categorylist = null;
    private ActionBarDrawerToggle actionbarToggler;
    private ViewPager pageViewer;
    private HashMap<String, Original_Data> hashmap = new HashMap<String, Original_Data>();
    private List<Fragment> FragArray_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        Intent intent = new Intent(MainActivity.this, ShowStory.class);
        startService(intent);
        enterDetails = new EnterDetails();
        Log.d(TAG, "Input obj is created");

        IntentFilter intentf = new IntentFilter(Utility.STORY_NEWS);
        registerReceiver(enterDetails, intentf);
        drawer_layout = findViewById(R.id.drawlayout);
        Log.d(TAG, "drawer_layout created");

        listviewItems = findViewById(R.id.showCaseMedia);
        listviewItems.setAdapter(new ArrayAdapter<>(this, R.layout.drawer, textlist));
        Log.d(TAG, "listviewItems created");

        listviewItems.setOnItemClickListener(
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { ItemClicked(position); }
                });
        actionbarToggler = new ActionBarDrawerToggle(this, drawer_layout, R.string.drawer_open_navigation, R.string.drawer_close_navigation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Log.d(TAG, "getSupportActionBar created");

        if (testInternetConn()) {
            if (savedInstanceState != null) {
                String title="title";
                setTitle(savedInstanceState.getCharSequence(title));
                String categorylist="categorylist";
                allocateRes((ArrayList<Original_Data>) savedInstanceState.getSerializable("sourcelist"),
                        savedInstanceState.getStringArrayList(categorylist));
            } else {
                Log.d(TAG, "savedInstanceState is null");
                MaterialHandler materialHandler = new MaterialHandler(this, "");
                materialHandler.retriveInfo(this);
            }
        } else { AlertBox(); }
        FragArray_List = getFragArray_List();
        pageadapter = new ViewPageAdapter(getSupportFragmentManager());
        Log.d(TAG, "pageadapter is set");

        pageViewer = (ViewPager) findViewById(R.id.mainviewpage);
        pageViewer.setAdapter(pageadapter);
        int pageLimit=10;
        pageViewer.setOffscreenPageLimit(pageLimit);
        String story_frag="NewsFragment";
        if (savedInstanceState != null) {
            String size="size";
            for (int i = 0; i < savedInstanceState.getInt(size); i++) { FragArray_List.add(getSupportFragmentManager().getFragment(savedInstanceState,
                    "story_frag" + Integer.toString(i))); }
        } else { pageViewer.setBackgroundResource(R.drawable.newsback); }
        pageadapter.notifyDataSetChanged();
    }
/////////////////////////////////////////
    public void AlertBox()
    {
        Log.d(TAG, "AlertBox");
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        alertdialogbuilder.setTitle("Network Error");
        String issue_const="Network Issue. Please retry.";
        alertdialogbuilder.setMessage(issue_const);
        String color="<font color='#254E58'>OK</font>";
        alertdialogbuilder.setNegativeButton(Html.fromHtml(color),
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int arg1) { }});
        alertdialogbuilder.show();
        AlertDialog alertdialog = alertdialogbuilder.create();
        Log.d(TAG, "showing AlertBox");
        alertdialog.show();
    }

    @Override
    protected void onPostCreate(Bundle postcreatestate) {
        super.onPostCreate(postcreatestate);
        actionbarToggler.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configchange) {
        super.onConfigurationChanged(configchange);
        actionbarToggler.onConfigurationChanged(configchange);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mainmenu) {
        this.menus = mainmenu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        if (actionbarToggler.onOptionsItemSelected(item)) { return true; }
        String EmptyString="";
        MaterialHandler sourceloader = new MaterialHandler(this, EmptyString + item);
        sourceloader.retriveInfo(this);
        return super.onOptionsItemSelected(item);
    }

    private void ItemClicked(int position) {
        pageViewer.setBackground(null);
        String title=textlist.get(position);
        setTitle(title);
        Intent mainintent = new Intent(Utility.SERVICE_ACTION);
        mainintent.putExtra(Utility.SOURCE_INFO, hashmap.get(textlist.get(position)));
        Log.d(TAG, "sending Broadcast");

        sendBroadcast(mainintent);
        drawer_layout.closeDrawer(listviewItems);
    }
//////////////////////////////////////
    public void allocateRes(ArrayList<Original_Data> sourcelist, ArrayList<String> categorylist) {
        clearList(textlist,hashmap);
        Collections.sort(sourcelist);
        for (Original_Data source : sourcelist) {
            textlist.add(source.getStory());
            hashmap.put(source.getStory(), source); }
        ((ArrayAdapter<String>) listviewItems.getAdapter()).notifyDataSetChanged();
        if (this.categorylist == null) {
            Log.d(TAG, "categorylist is null");
            this.categorylist = new ArrayList<>(categorylist);
            if (menus != null) {
                String Const_all="all";
                this.categorylist.add(0, Const_all);
                for (String c : this.categorylist) {
                    menus.add(c);
            }
         }
    }
    }

    private void clearList(ArrayList<String> textlist, HashMap<String, Original_Data> hashmap) {
        textlist.clear();
        hashmap.clear();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu mainmenu) {
        Log.d(TAG, "onPrepareOptionsMenu");

        if (this.categorylist != null) {
            mainmenu.clear();
            for (String categorylist : this.categorylist) { mainmenu.add(categorylist); }
        }
        Log.d(TAG, "returning super onPrepareOptionsMenu");

        return super.onPrepareOptionsMenu(mainmenu);
    }

    private List<Fragment> getFragArray_List() {
        List<Fragment> fraglist = new ArrayList<Fragment>();
        return fraglist;
    }

    @Override
    protected void onDestroy() {
            Log.d(TAG, "onDestroy");
            super.onDestroy();
            Intent mainint = new Intent(MainActivity.this, ShowStory.class);
            stopService(mainint);
            unregisterReceiver(enterDetails);

    }

    @Override
    protected void onStop() {

            super.onStop();
            Intent mainint = new Intent(MainActivity.this, ShowStory.class);
            stopService(mainint);
            unregisterReceiver(enterDetails);
    }

    @Override
    protected void onPause() {
            unregisterReceiver(enterDetails);
            super.onPause();
    }

    public boolean testInternetConn() {
        boolean bool = true;
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d(TAG, "testInternetConn");

        NetworkInfo network_value = connectivitymanager.getActiveNetworkInfo();
        if (network_value != null && network_value.isConnectedOrConnecting()) { return bool; }
        else { return !bool; }
    }
/////////////////////////////////////
    @Override
    public void onSaveInstanceState(Bundle bundlesavedInstanceState) {
        super.onSaveInstanceState(bundlesavedInstanceState);
        int zero=0;
        Log.d(TAG, "onSaveInstanceState");
        int total_count = zero;
        int counter = zero;
        for (counter = zero; counter < FragArray_List.size(); counter++)
        {
            if (FragArray_List.get(counter).isAdded())
            {
                total_count++;
                String storyFrag="NewsFragment";
                String newsfrag = storyFrag + counter;
                getSupportFragmentManager().putFragment(bundlesavedInstanceState, newsfrag, FragArray_List.get(counter));
            }
        }
        String size="size";
        bundlesavedInstanceState.putInt(size, total_count);
        String bucket_list="categorylist";
        bundlesavedInstanceState.putStringArrayList(bucket_list, categorylist);
        ArrayList<Original_Data> sourcelist = new ArrayList<>();
        for (String hasher : hashmap.keySet()) { sourcelist.add(hashmap.get(hasher)); }
        String source_list="sourcelist";
        bundlesavedInstanceState.putSerializable(source_list, sourcelist);
        String title="title";
        bundlesavedInstanceState.putCharSequence(title, getTitle());
    }

    public void setTitle() {
    }
    //////////////
    public class EnterDetails extends BroadcastReceiver {
        @Override
        public void onReceive(Context inputcontext, Intent inputintent) {
            switch (inputintent.getAction()) {
                case Utility.STORY_NEWS:
                    Log.d(TAG, "onReceive");
                    if (inputintent.hasExtra(Utility.STORY_DATA_ARTICLE)) {
                        story_Array_List = (ArrayList) inputintent.getSerializableExtra(Utility.STORY_DATA_ARTICLE);
                        modifyFrag(story_Array_List);
                    }
                    break;
            }
        }

        private void modifyFrag(List<Story> articlelist) {
            Log.d(TAG, "modifyFrag");
            if(!testInternetConn())
              AlertBox();
            int itr = 0;
            for (itr = 0; itr < pageadapter.getCount(); itr++)
            {
                pageadapter.notifyChangeInPosition(itr);
            }
            String emptyString="";
            FragArray_List.clear();

            itr = 0;
            for (itr = 0; itr < articlelist.size(); itr++)
              FragArray_List.add(Records.newInstance((articlelist.get(itr)), emptyString + itr, emptyString + articlelist.size()));
            pageadapter.notifyDataSetChanged();
            pageViewer.setCurrentItem(0);
            Log.d(TAG, "return modifyFrag");

        }
    }

    private class ViewPageAdapter extends FragmentPagerAdapter {
        public ViewPageAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return FragArray_List.size();
        }

        @Override
        public long getItemId(int position)
        {
            return itemID + position;
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position)
        {
            return FragArray_List.get(position);
        }

        public void notifyChangeInPosition(int position)
        {
            itemID += getCount() + position;
        }

        private long itemID = 0;

    }

}