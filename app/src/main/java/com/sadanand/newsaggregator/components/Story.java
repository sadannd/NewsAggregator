package com.sadanand.newsaggregator.components;

import java.io.Serializable;
public class Story implements Serializable {
    private String storyTitle,storyWriter,story_time, story_image, story_site,storyInformation;
    public Story() { }

    public void setStoryTitle(String storyTitle) {

        this.storyTitle = storyTitle;
    }
    public String getStoryWriter() {

        return storyWriter;
    }

    public void setStoryWriter(String storyWriter) {

        this.storyWriter = storyWriter;
    }

    public String getStoryTitle() {

        return storyTitle;
    }

    public void setStory_site(String story_site) {

        this.story_site = story_site;
    }

    public String getStoryInformation() {

        return storyInformation;
    }

    public void setStoryInformation(String storyInformation) {

        this.storyInformation = storyInformation;
    }

    public String getStory_site() {

        return story_site;
    }

    public void setStory_time(String story_time) {

        this.story_time = story_time;
    }
    public String getPicture() {

        return story_image;
    }

    public void setPicture(String image) {

        this.story_image = image;
    }

    public String getStory_time() {

        return story_time;
    }
}
