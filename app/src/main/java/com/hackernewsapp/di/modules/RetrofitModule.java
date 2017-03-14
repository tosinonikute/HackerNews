package com.hackernewsapp.di.modules;


import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.di.scopes.UserScope;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;


/**
 * @author Tosin Onikute.
 */

@Module
public class RetrofitModule {

    @Provides
    public StoryInterface providesStoryInterface(RestAdapter restAdapter) {
        return restAdapter.create(StoryInterface.class);
    }
}