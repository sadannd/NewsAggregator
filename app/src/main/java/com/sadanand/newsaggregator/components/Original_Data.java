package com.sadanand.newsaggregator.components;

import java.io.Serializable;
public class Original_Data implements Serializable, Comparable<Original_Data> {
    private String dataId, story, story_URL, story_category;
    public Original_Data() {
    }
    public void setDataId(String dataId) {

        this.dataId = dataId;
    }

    public String getDataId() {

        return dataId;
    }

    public void setStory_URL(String story_URL) {

        this.story_URL = story_URL;
    }
    public String getStory() {

        return story;
    }

    public void setStory(String story) {

        this.story = story;
    }

    public String getStory_URL() {

        return story_URL;
    }

    public int compareTo(Original_Data other) {

        return story.compareTo(other.story);
    }
    public String getStory_category() {

        return story_category;
    }

    public void setStory_category(String NewsCategory) {

        this.story_category = NewsCategory;
    }

}
