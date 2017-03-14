package com.hackernewsapp.discussion.presenter;

import android.content.Context;

import com.hackernewsapp.Story.model.Story;
import com.hackernewsapp.Story.view.StoryView;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.discussion.view.DiscussionView;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public interface DiscussionPresenter {

    public void setView(DiscussionView discussionView);

    public void getComments(final StoryInterface mInterface, CompositeSubscription mCompositeSubscription,
                            Context context, final Story story, final boolean updateObservable);

    public Observable<List<Discussion>> fetchComment(StoryInterface mInterface, int level, Story story);

    public Observable<List<Discussion>> getPartsComment(final StoryInterface mInterface, final int level, List<Long> cmtIds);

    public Observable<List<Discussion>> getSinglePartComments(final StoryInterface mInterface, final int level, final long cmtId);

    public Observable<List<Discussion>> getAllComments(final StoryInterface mInterface, final int level, final List<Long> firstLevelCmtIds);

    public Observable<Discussion> getInnerLevelComments(final StoryInterface mInterface, final int level, Discussion cmt);

    public List<Discussion> sortComments(List<Discussion> allDiscussions, List<Long> firstLevelCmtIds);

    public List<Discussion> sortAllComments(HashMap<Long, Discussion> allCommentsHashMap, List<Discussion> listOfDiscussions);





}
