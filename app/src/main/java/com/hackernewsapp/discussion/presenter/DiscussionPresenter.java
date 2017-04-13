package com.hackernewsapp.discussion.presenter;

import android.content.Context;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.discussion.view.DiscussionView;
import com.hackernewsapp.story.model.Story;

import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public interface DiscussionPresenter {

    public void setView(DiscussionView discussionView);

    public void getComments(final StoryInterface mInterface, CompositeSubscription mCompositeSubscription,
                            Context context, final Story story, final boolean updateObservable);


}
