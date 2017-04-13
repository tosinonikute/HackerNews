package com.hackernewsapp.story.data;

import android.app.Application;

import com.hackernewsapp.Constants;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.story.view.StoryView;
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
public class StoryInteractorImpl implements StoryInteractor {

    private final Logger logger = Logger.getLogger(getClass());
    private final Application application;

    private StoryView storyView;

    private int totalNo = 0;
    private List<Long> listStoryId = new ArrayList<>();
    private ArrayList<Story> listArrayList = new ArrayList<Story>();
    private ArrayList<Story> refreshedArrayList = new ArrayList<Story>();
    private Observable<Story> mStoryObservable;


    public StoryInteractorImpl(Application application){
        this.application = application;
    }


    public void sayHello(){
        //
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
//            listArrayList.add(storiesMap.get(id));
//            refreshedArrayList.add(storiesMap.get(id));
        }

        return orderedStoryList;
    }


}
