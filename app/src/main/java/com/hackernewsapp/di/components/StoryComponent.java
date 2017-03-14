package com.hackernewsapp.di.components;


import com.hackernewsapp.Story.MainActivity;
import com.hackernewsapp.Story.presenter.StoryPresenter;
import com.hackernewsapp.Story.view.StoryView;
import com.hackernewsapp.di.modules.DiscussionModule;
import com.hackernewsapp.di.modules.RetrofitModule;
import com.hackernewsapp.di.modules.StoryModule;
import com.hackernewsapp.di.scopes.UserScope;
import com.hackernewsapp.discussion.DiscussionActivity;

import dagger.Component;

/**
 * @author Tosin Onikute.
 */

@UserScope
@Component(dependencies = NetComponent.class, modules = {RetrofitModule.class, StoryModule.class, DiscussionModule.class})
public interface StoryComponent {

    void inject(MainActivity activity);
    void inject(DiscussionActivity activity);

}


