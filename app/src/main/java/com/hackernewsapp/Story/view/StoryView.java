package com.hackernewsapp.Story.view;

import com.hackernewsapp.Story.model.Story;

import java.util.ArrayList;

/**
 * Created by tosin on 3/13/2017.
 */

public interface StoryView {

    public void loadView();

    public void init();

    public void populateRecyclerView();

    public void pullToRefresh();

    public void refresh(String topStories, boolean refresh);

    public void implementScrollListener();

    public void setLayoutVisibility();

    public void displayOfflineSnackbar();

    public void hideOfflineSnackBar();

    public void doAfterFetchStory();

    public void setAdapter(Integer storLoaded, ArrayList<Story> listArrayList, ArrayList<Story> refreshedArrayList, boolean loadmore, Integer totalNum);


}
