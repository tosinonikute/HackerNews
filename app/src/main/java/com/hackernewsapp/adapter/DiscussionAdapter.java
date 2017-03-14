package com.hackernewsapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.hackernewsapp.R;
import com.hackernewsapp.discussion.model.Discussion;
import com.hackernewsapp.util.Misc;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tosin Onikute.
 */

public class DiscussionAdapter
        extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private ArrayList<Discussion> mDiscussion;
    Context mContext;
    RecyclerView recyclerView;


    int indentWidth;
    int colorBg;
    int colorOrange;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAuthor, mCommentTime, theComment, txtIndent;
        public RelativeLayout relativeLayout;
        // fonts
        public Typeface typeFace;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            mAuthor = (TextView) view.findViewById(R.id.author);
            mCommentTime = (TextView) view.findViewById(R.id.time);
            theComment = (TextView) view.findViewById(R.id.the_comment);
            txtIndent = (TextView) view.findViewById(R.id.indent);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative_comment_list);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAuthor.getText();
        }
    }

    public String getValueAt(int position) {
        return String.valueOf(mDiscussion.get(position).getId());
    }

    public DiscussionAdapter(Context context, ArrayList<Discussion> discussion) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mContext = context;
        mBackground = mTypedValue.resourceId;
        mDiscussion = discussion;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        /* Set your values */

        final Discussion model = (Discussion) mDiscussion.get(position);

        String author = "";
        Integer time = 0;
        String comment = "";

        if(model.getBy() != null) author = model.getBy();
        if(model.getTime() != null) time = model.getTime();
        if(model.getText() != null) {
            Spanned commentSpanned = Html.fromHtml(StringEscapeUtils.unescapeHtml(model.getText()));
            comment = commentSpanned.toString();
        }


        holder.mAuthor.setText(author);
        holder.mCommentTime.setText(String.valueOf(Misc.formatTime(time)));
        holder.theComment.setText(String.valueOf(comment));


        // set indent margins
        if( model.getLevel() != 0 ) {
            RelativeLayout.LayoutParams layoutParams =
                    new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int level = 0;

            for (int i = 0; i < model.getLevel(); i++) {
                level = level + 10;
            }

            layoutParams.setMargins(level, 0, 0, 0); // left, top, right, bottom
            holder.relativeLayout.setLayoutParams(layoutParams);
            holder.relativeLayout.requestLayout();

        }



    }

    @Override
    public int getItemCount() {
        return (null != mDiscussion ? mDiscussion.size() : 0);
    }

    public void addAll(List<Discussion> data){
        //mDiscussion.addAll(data);
        notifyDataSetChanged();
    }


}

