package com.hackernewsapp.story.presenter;

import android.app.Application;

import com.hackernewsapp.Constants;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.story.data.StoryInteractor;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.story.view.StoryView;
import com.hackernewsapp.util.Logger;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public class StoryPresenterImpl implements StoryPresenter{

    private final Logger logger = Logger.getLogger(getClass());
    private final Application application;

    private StoryView storyView;
    private StoryInteractor storyInteractor;


    private int totalNo = 0;
    private List<Long> listStoryId = new ArrayList<>();
    private ArrayList<Story> listArrayList = new ArrayList<Story>();
    private ArrayList<Story> refreshedArrayList = new ArrayList<Story>();
    private Observable<Story> mStoryObservable;


    public StoryPresenterImpl(Application application, StoryInteractor storyInteractor){
        this.application = application;
        this.storyInteractor = storyInteractor;
    }

    @Override
    public void setView(StoryView storyView){
        this.storyView = storyView;
    }

    // Method for repopulating recycler view
    @Override
    public void updateRecyclerView(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription,
                                   Integer fromIndex, Integer toIndex) {

        // Show Progress Layout
        refreshedArrayList.clear();
        storyView.setLayoutVisibility();

        logger.debug(String.valueOf(fromIndex) + " " + String.valueOf(toIndex));
        fetchStories(storyInterface, mCompositeSubscription, true, true, listStoryId.subList(fromIndex, toIndex));
    }


    @Override
    public void getStoryIds(final StoryInterface storyInterface, String storyTypeUrl,
                            final CompositeSubscription mCompositeSubscription, boolean refresh){
        if(refresh){
            listArrayList.clear();
            refreshedArrayList.clear();
        }

        if (storyInterface != null) {

            mCompositeSubscription.add( storyInterface.getStories(storyTypeUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<Long>>() {
                        @Override
                        public void call(List<Long> longs) {

                            listStoryId.clear();
                            listStoryId.addAll(longs);
                            totalNo = listStoryId.size();

                            fetchStories(storyInterface, mCompositeSubscription,
                                    true, false, listStoryId.subList(0, Constants.NO_OF_ITEMS_LOADING));

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            logger.debug(throwable.getLocalizedMessage());
                        }
                    }));

        }
    }


    public void fetchStories(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription,
                             boolean updateObservable, final boolean loadmore, List<Long> list) {
        if (mStoryObservable == null || updateObservable) {
            mStoryObservable = storyInteractor.getStorys(storyInterface, list).cache();
        }

        mCompositeSubscription.add(mStoryObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Story>() {
                    @Override
                    public void onCompleted() {
                        logger.debug("completed");

                        storyView.doAfterFetchStory();
                        int storiesLoaded = listArrayList.size();

                        storyView.setAdapter( storiesLoaded, listArrayList, refreshedArrayList, loadmore, totalNo);

                    }
                    @Override
                    public void onError(Throwable throwable) {
                        logger.debug(throwable.getLocalizedMessage());
                    }
                    @Override
                    public void onNext(Story story) {
                        if (story != null) {
                            listArrayList.add(story);
                            refreshedArrayList.add(story);
                        }
                    }
                }));


    }







}
