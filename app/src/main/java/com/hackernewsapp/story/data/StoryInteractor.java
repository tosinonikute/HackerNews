package com.hackernewsapp.story.data;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.story.model.Story;

import java.util.List;

import rx.Observable;

/**
 * @author Tosin Onikute.
 *
 * StoryInteractor is an interface that is implemented by the StoryInteractorImpl Data Manager
 *
 */

public interface StoryInteractor {

    public void sayHello();

    public Observable<Story> getStorys(StoryInterface storyInterface, List<Long> storyIds);

    public Observable<List<Story>> subListStories(StoryInterface storyInterface, final List<Long> storyIds);

    public List<Story> sortStories(List<Story> storyList, List<Long> storyIds);




}
