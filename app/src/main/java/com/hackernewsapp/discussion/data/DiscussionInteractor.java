package com.hackernewsapp.discussion.data;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.story.model.Story;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * @author Tosin Onikute.
 *
 * StoryInteractor is an interface that is implemented by the StoryInteractorImpl Data Manager
 *
 */

public interface DiscussionInteractor {

    public Observable<List<Discussion>> fetchComment(StoryInterface mInterface, int level, Story story);

    public Observable<List<Discussion>> getPartsComment(final StoryInterface mInterface, final int level, List<Long> cmtIds);

    public Observable<List<Discussion>> getSinglePartComments(final StoryInterface mInterface, final int level, final long cmtId);

    public Observable<List<Discussion>> getAllComments(final StoryInterface mInterface, final int level, final List<Long> firstLevelCmtIds);

    public Observable<Discussion> getInnerLevelComments(final StoryInterface mInterface, final int level, Discussion cmt);

    public List<Discussion> sortComments(List<Discussion> allDiscussions, List<Long> firstLevelCmtIds);

    public List<Discussion> sortAllComments(HashMap<Long, Discussion> allCommentsHashMap, List<Discussion> listOfDiscussions);


}
