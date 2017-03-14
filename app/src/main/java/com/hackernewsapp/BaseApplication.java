package com.hackernewsapp;

import android.app.Application;

import com.hackernewsapp.di.components.DaggerNetComponent;
import com.hackernewsapp.di.components.DaggerStoryComponent;
import com.hackernewsapp.di.components.NetComponent;
import com.hackernewsapp.di.components.StoryComponent;
import com.hackernewsapp.di.modules.AppModule;
import com.hackernewsapp.di.modules.DiscussionModule;
import com.hackernewsapp.di.modules.NetModule;
import com.hackernewsapp.di.modules.RetrofitModule;
import com.hackernewsapp.di.modules.StoryModule;

/**
 * @author Tosin Onikute.
 */

public class BaseApplication extends Application {

    private NetComponent mNetComponent;
    private StoryComponent mStoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule())
                .build();

        mStoryComponent = DaggerStoryComponent.builder()
                .netComponent(mNetComponent)
                .retrofitModule(new RetrofitModule())
                .storyModule(new StoryModule(this))
                .discussionModule(new DiscussionModule(this))
                .build();



    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public StoryComponent getStoryComponent() {
        return mStoryComponent;
    }




}
