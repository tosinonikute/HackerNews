package com.hackernewsapp.story;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hackernewsapp.BaseApplication;
import com.hackernewsapp.Constants;
import com.hackernewsapp.R;
import com.hackernewsapp.story.model.Story;
import com.hackernewsapp.story.presenter.StoryPresenter;
import com.hackernewsapp.story.view.StoryView;
import com.hackernewsapp.StoryInterface;
import com.hackernewsapp.adapter.ListingAdapter;
import com.hackernewsapp.util.NetworkUtil;
import com.hackernewsapp.util.ui.MaterialProgressBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;


public class MainActivity extends AppCompatActivity implements StoryView {

    @Inject
    SharedPreferences mSharedPreferences;

    @Inject
    StoryInterface storyInterface;

    @Inject
    StoryPresenter storyPresenter;

    private RelativeLayout storyLayout;
    private static RecyclerView listRecyclerView;


    private static List<Story> storyList;

    private static ListingAdapter adapter;

    private static RelativeLayout bottomLayout;
    private static LinearLayoutManager mLayoutManager;

    // Variables for scroll listener
    private boolean userScrolled = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private int storiesLoaded = 0;

    private int page = 1;

    private ArrayList<Long> data = new ArrayList<Long>();

    private CompositeSubscription mCompositeSubscription;
    private int totalNo = 0;

    private MaterialProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;
    private Snackbar snackbarOffline;
    private int tempNo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BaseApplication) getApplication()).getStoryComponent().inject(this);
        storyPresenter.setView(this);

        storyLayout = (RelativeLayout) findViewById(R.id.layout_story_root);
        mCompositeSubscription = new CompositeSubscription();

        init();
        loadView();



    }

    public void loadView(){
        if(NetworkUtil.isConnected(getApplicationContext())) {
            populateRecyclerView();
            implementScrollListener();
            pullToRefresh();
            hideOfflineSnackBar();
        } else {
            displayOfflineSnackbar();
        }
    }

    // Initialize the view
    public void init() {
        bottomLayout = (RelativeLayout) findViewById(R.id.load_more_items);
        progressBar = (MaterialProgressBar) findViewById(R.id.material_progress_bar);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listRecyclerView = (RecyclerView) findViewById(R.id.stories_recyclerview);
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setLayoutManager(mLayoutManager);// for
    }

    // populate the list view by adding data to arraylist
    public void populateRecyclerView() {

        progressBar.setVisibility(View.VISIBLE);
        storyPresenter.getStoryIds(storyInterface, Constants.TOP_STORIES, mCompositeSubscription, false);

    }

    public void pullToRefresh(){
        // Pull to refresh
        if(NetworkUtil.isConnected(getApplicationContext())) {

            // Lookup the swipe container view
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    refresh(Constants.TOP_STORIES, true);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        } else {
            displayOfflineSnackbar();
        }
    }

    public void refresh(String topStories, boolean refresh){
        totalNo = 0;
        storiesLoaded = 0;
        mCompositeSubscription.clear();
        // reset the adapter
        storyPresenter.getStoryIds(storyInterface, topStories, mCompositeSubscription, refresh);
    }



    public void implementScrollListener() {

        listRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // If scroll state is touch scroll then set userScrolled
                // true
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                // Here get the child count, item count and visibleitems
                // from layout manager

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                // Now check if userScrolled is true and also check if
                // the item is end then update recycler view and set
                // userScrolled to false
                if (userScrolled
                        && (visibleItemCount + pastVisiblesItems) == totalItemCount) {
                    userScrolled = false;
                    page = page + 1;

                    int nextNumber = storiesLoaded + 1;
                    int remaining = totalNo - storiesLoaded;

                    if(remaining >= storiesLoaded + Constants.NO_OF_ITEMS_LOADING) {
                        storyPresenter.updateRecyclerView(storyInterface, mCompositeSubscription,storiesLoaded, storiesLoaded + Constants.NO_OF_ITEMS_LOADING);

                    } else {
                        storyPresenter.updateRecyclerView(storyInterface, mCompositeSubscription, storiesLoaded, totalNo);
                    }
                }

            }

        });

    }



    public void setLayoutVisibility(){
        bottomLayout.setVisibility(View.VISIBLE);
    }


    public void setAdapter(Integer storLoaded, ArrayList<Story> listArrayList,
                           ArrayList<Story> refreshedArrayList, boolean loadmore, Integer totalNum){
        storiesLoaded = storLoaded;
        totalNo = totalNum;
        if(listArrayList.size() != 0) {
            if(!loadmore) {

                adapter = new ListingAdapter(getApplicationContext(), listArrayList);
                listRecyclerView.setAdapter(adapter);// set adapter on recyclerview
                adapter.notifyDataSetChanged();// Notify the adapter
            } else {
                adapter.addAll(refreshedArrayList);
            }
        }
    }

    public void doAfterFetchStory(){

        progressBar.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        swipeContainer.setRefreshing(false);
    }


    public void displayOfflineSnackbar() {
        snackbarOffline = Snackbar.make(storyLayout, R.string.no_connection_snackbar, Snackbar.LENGTH_INDEFINITE);
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

    public void hideOfflineSnackBar() {
        if (snackbarOffline != null && snackbarOffline.isShown()) {
            snackbarOffline.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}
