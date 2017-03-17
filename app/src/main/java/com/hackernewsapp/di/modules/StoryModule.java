package com.hackernewsapp.di.modules;


import android.app.Application;

import com.hackernewsapp.story.presenter.StoryPresenter;
import com.hackernewsapp.story.presenter.StoryPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * @author Tosin Onikute.
 */

@Module
public class StoryModule {

    private Application application;

    public StoryModule(Application application){
        this.application = application;
    }

    @Provides
    public StoryPresenter getStoryPresenter(){
        return new StoryPresenterImpl(application);
    }


}