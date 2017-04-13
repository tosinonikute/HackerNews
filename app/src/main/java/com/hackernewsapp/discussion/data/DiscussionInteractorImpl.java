package com.hackernewsapp.discussion.data;

import android.app.Application;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * @author Tosin Onikute.
 *
 * This is a Data Manager implementer class which contains methods, exposed for all the story related data handling operations
 * to decouple your class, thus making it cleaner and testable
 *
 */


@Singleton
public class DiscussionInteractorImpl implements DiscussionInteractor {

    private final Logger logger = Logger.getLogger(getClass());
    private final Application application;
    private Observable<List<Discussion>> mCommentListObservable;


    public DiscussionInteractorImpl(Application application){
        this.application = application;
    }

    @Override
    public Observable<List<Discussion>> fetchComment(StoryInterface mInterface, int level, Story story) {
        List<Long> allCommentIds = story.getKids();
        long descendants = story.getDescendants();



        if ( descendants > 15 && allCommentIds.size() > 3 ) {
            // Get kids when kids size greater than 3";
            return Observable.concat(getPartsComment(mInterface, level, allCommentIds.subList(0, 3) ),
                    getAllComments(mInterface, level, allCommentIds.subList(3, allCommentIds.size())));
        } else if (descendants / allCommentIds.size() > 15) {
            // Get kids when kids size greater than 15
            return getPartsComment(mInterface, level, allCommentIds);
        } else {
            // If no criteria, use default
            return getAllComments(mInterface, level, allCommentIds);
        }
    }

    @Override
    public Observable<List<Discussion>> getPartsComment(final StoryInterface mInterface, final int level, List<Long> cmtIds) {
        return Observable.from(cmtIds)
                .flatMap(new Func1<Long, Observable<List<Discussion>>>() {
                    @Override
                    public Observable<List<Discussion>> call(Long cmtId) {
                        return getSinglePartComments(mInterface, level, cmtId);
                    }
                });
    }

    @Override
    public Observable<List<Discussion>> getSinglePartComments(final StoryInterface mInterface, final int level, final long cmtId) {
        return mInterface.getComment(cmtId)
                .onErrorReturn(new Func1<Throwable, Discussion>() {
                    @Override
                    public Discussion call(Throwable thr) {
                        return null;
                    }
                })
                .filter(new Func1<Discussion, Boolean>() {
                    @Override
                    public Boolean call(Discussion cmt) {
                        return (cmt != null) && !cmt.getRemoved() &&  cmt.getText() != null;
                    }
                })
                .flatMap(new Func1<Discussion, Observable<Discussion>>() {
                    @Override
                    public Observable<Discussion> call(Discussion cmt) {
                        return getInnerLevelComments(mInterface,level, cmt);
                    }
                })
                .toList()
                .map(new Func1<List<Discussion>, List<Discussion>>() {
                    @Override
                    public List<Discussion> call(List<Discussion> allDiscussions) {
                        List<Long> listFirstLevelComments = new ArrayList<Long>();
                        listFirstLevelComments.add(cmtId);
                        return sortComments(allDiscussions, listFirstLevelComments);
                    }
                });
    }

    @Override
    public Observable<List<Discussion>> getAllComments(final StoryInterface mInterface, final int level, final List<Long> firstLevelCmtIds) {

        return Observable.from(firstLevelCmtIds)
                .flatMap(new Func1<Long, Observable<Discussion>>() {
                    @Override
                    public Observable<Discussion> call(Long commentId) {
                        return mInterface.getComment(commentId)
                                .onErrorReturn(new Func1<Throwable, Discussion>() {
                                    @Override
                                    public Discussion call(Throwable thr) {
                                        return null;
                                    }
                                });
                    }
                })
                .filter(new Func1<Discussion, Boolean>() {
                    @Override
                    public Boolean call(Discussion cmt) {
                        return (cmt != null) && !cmt.getRemoved() &&  cmt.getText() != null;
                    }
                })
                .flatMap(new Func1<Discussion, Observable<Discussion>>() {
                    @Override
                    public Observable<Discussion> call(Discussion cmt) {
                        return getInnerLevelComments(mInterface, level, cmt);
                    }
                })
                .toList()
                .map(new Func1<List<Discussion>, List<Discussion>>() {
                    @Override
                    public List<Discussion> call(List<Discussion> allDiscussions) {
                        return sortComments(allDiscussions, firstLevelCmtIds);
                    }
                });
    }

    @Override
    public Observable<Discussion> getInnerLevelComments(final StoryInterface mInterface, final int level, Discussion cmt) {
        if (cmt == null || cmt.getRemoved() || cmt.getText() == null) {
            return null;
        }
        cmt.setLevel(level);
        if ( cmt.getKids() != null && !cmt.getKids().isEmpty() ) {
            return Observable.just(cmt)
                    .mergeWith(Observable.from(cmt.getKids())
                            .flatMap(new Func1<Long, Observable<Discussion>>() {
                                @Override
                                public Observable<Discussion> call(Long cmtId) {
                                    return mInterface.getComment(cmtId)
                                            .onErrorReturn(new Func1<Throwable, Discussion>() {
                                                @Override
                                                public Discussion call(Throwable thr) {
                                                    return null;
                                                }
                                            });
                                }
                            })
                            .filter(new Func1<Discussion, Boolean>() {
                                @Override
                                public Boolean call(Discussion cmt) {
                                    return (cmt != null) && !cmt.getRemoved() && cmt.getText() != null;
                                }
                            })
                            .flatMap(new Func1<Discussion, Observable<Discussion>>() {
                                @Override
                                public Observable<Discussion> call(Discussion cmt) {
                                    // get all the other level of comments
                                    return getInnerLevelComments(mInterface, level + 1, cmt);
                                }
                            })
                    );
        }
        return Observable.just(cmt);
    }

    @Override
    public List<Discussion> sortComments(List<Discussion> allDiscussions, List<Long> firstLevelCmtIds) {
        HashMap<Long, Discussion> newHashMap = new HashMap<>();
        for (Discussion child : allDiscussions) {
            newHashMap.put(child.getId(), child);
        }
        List<Discussion> newlistFirstLevelCmt = new ArrayList<Discussion>();
        for (Long id : firstLevelCmtIds) {
            Discussion firstLevelCmt = newHashMap.get(id);
            if (firstLevelCmt != null && !firstLevelCmt.getRemoved() &&  firstLevelCmt.getText() != null) {
                newlistFirstLevelCmt.add(firstLevelCmt);
            }
        }
        return sortAllComments(newHashMap, newlistFirstLevelCmt);
    }

    @Override
    public List<Discussion> sortAllComments(HashMap<Long, Discussion> allCommentsHashMap, List<Discussion> listOfDiscussions) {
        List<Discussion> sortedDiscussionList = new ArrayList<Discussion>();

        for ( Discussion discussion : listOfDiscussions) {
            sortedDiscussionList.add(discussion);
            if ( discussion.getKids() != null && discussion.getKids().size() > 0 ) {
                List<Discussion> innerChildList = new ArrayList<Discussion>();
                for ( long id : discussion.getKids() ) {
                    Discussion child = allCommentsHashMap.get(id);
                    if (child != null && !child.getRemoved() &&  child.getText() != null) {
                        innerChildList.add(child);
                    }
                }
                sortedDiscussionList.addAll(sortAllComments(allCommentsHashMap, innerChildList));
            }
        }

//        discussionArrayList = new ArrayList<Discussion>(sortedDiscussionList);
        return sortedDiscussionList;
    }





}
