package com.hackernewsapp.discussion.presenter;

import android.app.Application;
import android.content.Context;

import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.discussion.data.DiscussionInteractor;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.discussion.view.DiscussionView;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.util.Logger;
import com.hackernewsapp.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public class DiscussionPresenterImpl implements DiscussionPresenter {


    private final Logger logger = Logger.getLogger(getClass());
    private final Application application;
    private DiscussionView discussionView;
    private DiscussionInteractor discussionInteractor;

    private Observable<List<Discussion>> mCommentListObservable;
    private ArrayList<Discussion> discussionArrayList;



    public DiscussionPresenterImpl(Application application, DiscussionInteractor discussionInteractor){
        this.application = application;
        this.discussionInteractor = discussionInteractor;
    }

    @Override
    public void setView(DiscussionView discussionView){
        this.discussionView = discussionView;
    }



    public void getComments(final StoryInterface mInterface, CompositeSubscription mCompositeSubscription, Context context, final Story story, final boolean updateObservable) {
        if(!NetworkUtil.isConnected(context)) {
            discussionView.displayOfflineSnackbar();
            return;
        }

        discussionView.setProgressBarVisible();

        if (story.getKids() != null && !story.getKids().isEmpty()) {
            if (mCommentListObservable == null || updateObservable) {
                mCommentListObservable = discussionInteractor.fetchComment(mInterface, 0, story).cache();
            }

            mCompositeSubscription.add(mCommentListObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Discussion>>() {
                        @Override
                        public void onCompleted() {

                            discussionView.setProgressBarGone();
                            discussionView.setAdapter(discussionArrayList);
                        }

                        @Override
                        public void onError(Throwable thr) {
                            logger.debug(thr.getMessage().toString());
                            discussionView.setProgressBarGone();
                        }

                        @Override
                        public void onNext(List<Discussion> listDiscussion) {

                            if (listDiscussion != null) {
                                discussionArrayList = new ArrayList<Discussion>(listDiscussion);
                            }

                        }
                    })
            );
        } else {
            // update the view to say no comment yet
            discussionView.sayNoComment();

        }
    }





}
