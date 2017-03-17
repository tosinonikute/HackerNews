package com.hackernewsapp.story.presenter;

import android.app.Application;

import com.hackernewsapp.Constants;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.story.view.StoryView;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Tosin Onikute.
 */

public class StoryPresenterImpl implements StoryPresenter{

    private final Logger logger = Logger.getLogger(getClass());
    private final Application application;
    private StoryView storyView;

    private int totalNo = 0;
    private List<Long> listStoryId = new ArrayList<>();
    private static ArrayList<Story> listArrayList = new ArrayList<Story>();
    private static ArrayList<Story> refreshedArrayList = new ArrayList<Story>();
    private Observable<Story> mStoryObservable;



    public StoryPresenterImpl(Application application){
        this.application = application;
    }

    @Override
    public void setView(StoryView storyView){
        this.storyView = storyView;
    }




    // Method for repopulating recycler view
    public void updateRecyclerView(StoryInterface storyInterface, CompositeSubscription mCompositeSubscription, Integer fromIndex, Integer toIndex) {

        // Show Progress Layout
        refreshedArrayList.clear();
        storyView.setLayoutVisibility();

        logger.debug(String.valueOf(fromIndex) + " " + String.valueOf(toIndex));
        fetchStories(storyInterface, mCompositeSubscription, true, true, listStoryId.subList(fromIndex, toIndex));
    }


    public void getStoryIds(final StoryInterface storyInterface, String storyTypeUrl,
                            final CompositeSubscription mCompositeSubscription, boolean refresh) {


        if(refresh){
            listArrayList.clear();
            refreshedArrayList.clear();
        }

        if (storyInterface != null) {
            mCompositeSubscription.add(storyInterface.getStories(storyTypeUrl)
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
            mStoryObservable = getStorys(storyInterface, list).cache();
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

                        }
                    }
                }));
    }


    public Observable<Story> getStorys(final StoryInterface storyInterface, List<Long> storyIds) {
        if (storyIds.size() > Constants.NO_OF_SPLIT_ITEMS * 2) {
            return Observable.concat(
                    subListStories(storyInterface, storyIds.subList(0, Constants.NO_OF_SPLIT_ITEMS)),
                    subListStories(storyInterface, storyIds.subList(Constants.NO_OF_SPLIT_ITEMS,
                            Constants.NO_OF_SPLIT_ITEMS * 2)),
                    subListStories(storyInterface, storyIds.subList(Constants.NO_OF_SPLIT_ITEMS * 2,
                            storyIds.size())))
                    .flatMap(new Func1<List<Story>, Observable<Story>>() {
                        @Override
                        public Observable<Story> call(List<Story> posts) {
                            return Observable.from(posts);
                        }
                    });
        } else {
            return subListStories(storyInterface, storyIds)
                    .flatMap(new Func1<List<Story>, Observable<Story>>() {
                        @Override
                        public Observable<Story> call(List<Story> storys) {
                            return Observable.from(storys);
                        }
                    });
        }
    }

    public Observable<List<Story>> subListStories(final StoryInterface storyInterface, final List<Long> storyIds) {
        return Observable.from(storyIds)
                .flatMap(new Func1<Long, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(Long aLong) {
                        return storyInterface.getStory(String.valueOf(aLong));
                    }
                })
                .onErrorReturn(new Func1<Throwable, Story>() {
                    @Override
                    public Story call(Throwable throwable) {
                        return null;
                    }
                })
                .filter(new Func1<Story, Boolean>() {
                    @Override
                    public Boolean call(Story story) {
                        return story != null && story.getTitle() != null;
                    }
                })
                .toList()
                .map(new Func1<List<Story>, List<Story>>() {
                    @Override
                    public List<Story> call(List<Story> stories) {
                        return sortStories(stories, storyIds);
                    }
                });
    }



    public List<Story> sortStories(List<Story> storyList, List<Long> storyIds) {
        HashMap<Long, Story> storiesMap = new HashMap<>();
        List<Story> orderedStoryList = new ArrayList<>();
        for (Story story : storyList) {
            storiesMap.put(story.getId(), story);
        }
        for (Long id : storyIds) {
            orderedStoryList.add(storiesMap.get(id));
            listArrayList.add(storiesMap.get(id));
            refreshedArrayList.add(storiesMap.get(id));
        }

        return orderedStoryList;
    }





}
