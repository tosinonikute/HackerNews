package com.hackernewsapp.di.modules;


import android.app.Application;

import com.hackernewsapp.Story.presenter.StoryPresenter;
import com.hackernewsapp.Story.presenter.StoryPresenterImpl;
import com.hackernewsapp.Story.view.StoryView;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.di.scopes.UserScope;

import dagger.Binds;
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