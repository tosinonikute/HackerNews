package com.hackernewsapp.discussion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.hackernewsapp.BaseApplication;
import com.hackernewsapp.R;
import com.hackernewsapp.Story.model.Story;
import com.hackernewsapp.Story.presenter.StoryPresenter;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.adapter.DiscussionAdapter;
import com.hackernewsapp.adapter.ListingAdapter;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.discussion.presenter.DiscussionPresenter;
import com.hackernewsapp.discussion.view.DiscussionView;
import com.hackernewsapp.util.Logger;
import com.hackernewsapp.util.Misc;
import com.hackernewsapp.util.NetworkUtil;
import com.hackernewsapp.util.ui.MaterialProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DiscussionActivity extends AppCompatActivity implements DiscussionView {


    private CoordinatorLayout commentLayout;
    private int position;
    private ArrayList<Story> mStory;
    private CompositeSubscription mCompositeSubscription;

    private String aTitle;

    private TextView headerTitle, headerUrl, headerPoints,
            headerComments, headerTime, headerPoster, headerContent;

    private static ArrayList<Discussion> discussionArrayList;
    private static DiscussionAdapter adapter;
    private static RecyclerView commentRecyclerView;
    private static LinearLayoutManager mLayoutManager;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private RelativeLayout layoutComment;
    private MaterialProgressBar progressBar;
    private TextView noComment;
    private Snackbar snackbarOffline;
    private Story mainStory;
    private FloatingActionButton floatingActionButton;
    private String storyDirectUrl = "";

    @Inject
    StoryInterface storyInterface;

    @Inject
    DiscussionPresenter discussionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);
        ((BaseApplication) getApplication()).getStoryComponent().inject(this);
        discussionPresenter.setView(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position = extras.getInt("position");
            mStory = (ArrayList<Story>) getIntent().getSerializableExtra("mStory");
            aTitle = extras.getString("title");
            storyDirectUrl = extras.getString("storyDirectUrl");
            if (aTitle != null) {
                setCollapseToolbar(aTitle);
            }
        }



        init();
        mCompositeSubscription = new CompositeSubscription();
        mainStory = mStory.get(position);
        progressBar = (MaterialProgressBar) findViewById(R.id.material_progress_bar);
        progressBar.bringToFront();
        noComment = (TextView) findViewById(R.id.no_comment_text);
        noComment.bringToFront();
        loadView();


    }

    // Initialize the view
    public void init() {

        fabButtonSetup();
        commentLayout = (CoordinatorLayout) findViewById(R.id.main_comment_content);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setLayoutManager(mLayoutManager);

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerUrl = (TextView) findViewById(R.id.header_url);
        headerPoints = (TextView) findViewById(R.id.header_points);
        headerComments = (TextView) findViewById(R.id.header_comments);
        headerTime = (TextView) findViewById(R.id.header_time);
        headerPoster = (TextView) findViewById(R.id.header_poster);
        headerContent = (TextView) findViewById(R.id.header_content);

    }

    public void loadView(){
        if(NetworkUtil.isConnected(getApplicationContext())) {
            if(mainStory != null) {
                setCommentHeader(mainStory);
                discussionPresenter.getComments(storyInterface, mCompositeSubscription, getApplicationContext(), mainStory, true);
            }
        } else {
            displayOfflineSnackbar();
        }
    }

    public void setCommentHeader(Story story){
        String title = "";
        String url = "";
        Integer points = 0;
        Integer time = 0;
        Integer commentsNo = 0;
        String poster = "";

        if(story.getTitle() != null) title = story.getTitle();
        if(story.getUrl() != null) url = story.getUrl();
        if(story.getScore() != null) points = story.getScore();
        if(story.getTime() != null) time = story.getTime();
        if(story.getDescendants() != null) commentsNo = story.getDescendants();
        if(story.getBy() != null) poster = story.getBy();

        headerTitle.setText(title);
        headerUrl.setText(url);
        headerPoints.setText(points.toString() + getResources().getString(R.string.story_point_p));
        if(points < 2) {
            headerComments.setText(commentsNo.toString() + " " + getResources().getString(R.string.comment_string_one));
        } else {
            headerComments.setText(commentsNo.toString() + " " + getResources().getString(R.string.comment_string));
        }
        headerTime.setText(String.valueOf(Misc.formatTime(time)));
        headerPoster.setText(getResources().getString(R.string.comment_by) + poster);

    }




    public void setCollapseToolbar(String title){

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        //collapsingToolbar.setTitle(title);
        collapsingToolbar.setTitle("");
        layoutComment = (RelativeLayout) findViewById(R.id.layout_comment_header_content);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Collapsed
                    layoutComment.setVisibility(View.GONE);
                } else if (verticalOffset == 0) {
                    // Expanded
                    layoutComment.setVisibility(View.VISIBLE);
                } else {
                    // Somewhere in between
                    layoutComment.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    public void displayOfflineSnackbar() {
        snackbarOffline = Snackbar.make(commentLayout, R.string.no_connection_snackbar, Snackbar.LENGTH_INDEFINITE);
        TextView snackbarText = (TextView) snackbarOffline.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setTextColor(getApplicationContext().getResources().getColor(android.R.color.white));
        snackbarOffline.setAction(R.string.snackbar_action_retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadView();
            }
        });
        snackbarOffline.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbarOffline.show();
    }

    public void setProgressBarVisible(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setProgressBarGone(){
        progressBar.setVisibility(View.GONE);
    }

    public void sayNoComment(){
        progressBar.setVisibility(View.GONE);
        if(progressBar.getVisibility() == View.GONE) {
            noComment.setVisibility(View.VISIBLE);
        }
    }

    public void setAdapter(ArrayList<Discussion> discussionArrayList){
        if(discussionArrayList.size() != 0) {

            adapter = new DiscussionAdapter(getApplicationContext(), discussionArrayList);
            commentRecyclerView.setAdapter(adapter);// set adapter on recyclerview
            adapter.notifyDataSetChanged();// Notify the adapter
        }
    }

    public void fabButtonSetup(){
        floatingActionButton = (FloatingActionButton) findViewById(R.id.float_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabButtonLink(v);
            }
        });
    }

    public void fabButtonLink(View v){
        if(v != null && storyDirectUrl != null) {
            Misc.displayLongToast(getApplicationContext(), "Opening web view!");
            Context context = v.getContext();
            Intent intent = new Intent(context, WebviewActivity.class);
            intent.putExtra("EXTRA_URL", storyDirectUrl);
            Activity activity = (Activity) v.getContext();
            activity.startActivityForResult(intent, 500);
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }







}
