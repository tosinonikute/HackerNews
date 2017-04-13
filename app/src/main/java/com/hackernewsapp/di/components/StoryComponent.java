package com.hackernewsapp.di.components;


import com.hackernewsapp.di.modules.DiscussionModule;
import com.hackernewsapp.di.modules.RetrofitModule;
import com.hackernewsapp.di.modules.StoryModule;
import com.hackernewsapp.di.scopes.UserScope;
import com.hackernewsapp.discussion.DiscussionActivity;
import com.hackernewsapp.story.MainActivity;
import com.hackernewsapp.story.data.StoryInteractor;
import com.hackernewsapp.story.presenter.StoryPresenter;

import dagger.Component;

/**
 * @author Tosin Onikute.
 */


@UserScope
@Component(dependencies = NetComponent.class, modules = {RetrofitModule.class, StoryModule.class, DiscussionModule.class})
public interface StoryComponent {

    void inject(MainActivity activity);
    void inject(DiscussionActivity activity);
    void inject(StoryPresenter storyPresenter);
    void inject(StoryInteractor storyInteractor);


}


