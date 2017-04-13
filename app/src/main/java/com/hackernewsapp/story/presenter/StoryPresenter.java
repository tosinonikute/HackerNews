package com.hackernewsapp.story.presenter;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.story.view.StoryView;

import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public interface StoryPresenter {

    public void setView(StoryView storyView);

    public void updateRecyclerView(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription,
                                   Integer fromIndex, Integer toIndex);

    public void getStoryIds(StoryInterface storyInterface, String storyTypeUrl,
                            CompositeSubscription mCompositeSubscription, boolean refresh);


}
