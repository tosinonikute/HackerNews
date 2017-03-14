package com.hackernewsapp.discussion.view;

import android.view.View;

import com.hackernewsapp.Story.model.Story;
import com.hackernewsapp.discussion.model.Discussion;

import java.util.ArrayList;

/**
 * Created by tosin on 3/13/2017.
 */

public interface DiscussionView {


    public void init();

    public void loadView();

    public void setCommentHeader(Story story);

    public void setCollapseToolbar(String title);

    public void displayOfflineSnackbar();

    public void setProgressBarVisible();

    public void setProgressBarGone();

    public void sayNoComment();

    public void setAdapter(ArrayList<Discussion> discussionArrayList);

    public void fabButtonSetup();

   public void  fabButtonLink(View v);



}
