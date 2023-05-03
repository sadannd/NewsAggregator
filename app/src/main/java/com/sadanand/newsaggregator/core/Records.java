package com.sadanand.newsaggregator.core;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pjnaik.newsaggregator.R;
import com.sadanand.newsaggregator.components.Story;
import com.sadanand.newsaggregator.helper.Utility;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Records extends Fragment implements View.OnClickListener{
    ImageView pictureStory;
    private Story stories;

    public static final Records newInstance(Story story, String indices, String count) {

        Records records = new Records();
        Bundle bundle = new Bundle(1);
        fillBundle(bundle,story,indices,count);
        records.setArguments(bundle);
        return records;
    }

    private static void fillBundle(Bundle bundle,Story story, String indices, String count) {
        bundle.putSerializable(getArticlesdata(), story);
        bundle.putString(getPositiondata(), indices);
        bundle.putString(getNewsdata(), count);
    }

    @NonNull
    private static String getNewsdata() {

        return Utility.STORY_TOTAL;
    }

    @NonNull
    private static String getArticlesdata() {

        return Utility.STORY_DATA;
    }
    @NonNull
    private static String getPositiondata() {

        return Utility.POSITION;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewgroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.story_data, viewgroup, false);
        String pos = getArguments().getString(Utility.POSITION);
        stories = (Story) getArguments().getSerializable(Utility.STORY_DATA);
        String storyCount = getArguments().getString(Utility.STORY_TOTAL);
        TextView timestamp = view.findViewById(R.id.story_time);
        pictureStory = view.findViewById(R.id.story_Image);

        TextView headLine = view.findViewById(R.id.story_header);

        headLine.setOnClickListener(this);
        TextView desc = view.findViewById(R.id.story_sneak_peek);

        desc.setOnClickListener(this);
        pictureStory.setOnClickListener(this);

        headLine.setText(stories.getStoryTitle());
        headLine.setText(stories.getStoryTitle());
        TextView writer = view.findViewById(R.id.story_writer);

        writer.setText(stories.getStoryWriter());
        desc.setText(stories.getStoryInformation());
        TextView count = view.findViewById(R.id.story_nos);

        count.setText(Integer.parseInt(pos) + 1 + " of " + storyCount);

        if (stories.getStory_time() != null) {
            String dateFormat="yyyy-MM-dd'T'HH:mm:ss'Z'";
            DateFormat date = new SimpleDateFormat(dateFormat);
            date.setLenient(false);
            String newDateF="MMM dd, yyyy hh:mmaa";
            DateFormat newDate = new SimpleDateFormat(newDateF);
            newDate.setLenient(false);
            String changed = stories.getStory_time();
            Date changedDate;
            int itr = 0;
            int outline = 2;
            boolean bool = false;
            while (!bool) {
                try {
                    changedDate = date.parse(changed);
                    timestamp.setText(newDate.format(changedDate));
                    bool = true;
                } catch (Exception e) {
                    String dateF="yyyy-MM-dd'T'HH:mm:ssz";
                    date = new SimpleDateFormat(dateF);
                    if (++itr == outline) {
                        bool = true;
                        String emptyString="";
                        timestamp.setText(emptyString);
                    }
                }
            }
        }
        if (stories.getPicture().length() > 0)
            insertPhoto(stories.getPicture(), view);
        else
            insertPhoto("null", view);
        return view;
    }

    private void insertPhoto(String url, View v) {
        Picasso picasso = new Picasso.Builder(this.getContext()).listener((picasso1, uri, exception) -> picasso1.load(R.drawable.brokenimage).into(pictureStory)).build();
        picasso.load(url).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(pictureStory);
    }

    @Override
    public void onClick(View view) {
        Intent Storyintent = new Intent(Intent.ACTION_VIEW);
        Storyintent.setData(Uri.parse(stories.getStory_site()));
        startActivity(Storyintent);
    }

    @Override
    public void onSaveInstanceState(Bundle savestate) {

        super.onSaveInstanceState(savestate);
    }

    @Override
    public void onActivityCreated(Bundle articlebundle) {
        super.onActivityCreated(articlebundle);
        if (articlebundle != null) { }
    }

}