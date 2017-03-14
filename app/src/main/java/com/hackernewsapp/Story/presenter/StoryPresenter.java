package com.hackernewsapp.Story.presenter;

import com.hackernewsapp.Story.model.Story;
import com.hackernewsapp.Story.view.StoryView;
import com.hackernewsapp.StoryInterface;

import java.util.List;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public interface StoryPresenter {

    public void setView(StoryView storyView);

    public void getStoryIds(StoryInterface storyInterface, String storyTypeUrl,
                            CompositeSubscription mCompositeSubscription, boolean refresh);

    public void fetchStories(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription,
                 boolean updateObservable, final boolean loadmore, List<Long> list);

    public Observable<Story> getStorys(StoryInterface storyInterface, List<Long> storyIds);

    public Observable<List<Story>> subListStories(StoryInterface storyInterface, final List<Long> storyIds);

    public List<Story> sortStories(List<Story> storyList, List<Long> storyIds);

    public void updateRecyclerView(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription, Integer fromIndex, Integer toIndex);



}
