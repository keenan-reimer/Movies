package app.com.example.keenanreimer.movies.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Vector;

import app.com.example.keenanreimer.movies.R;
import app.com.example.keenanreimer.movies.utility.TmdbHelper;

/**
 * Created by keenan.reimer on 9/18/2015.
 */
public class ReviewLinearLayout extends LinearLayout {

    LayoutInflater mLayoutInflater;
    Palette mPalette;

    private static final int KEY_KEY = 10;

    public ReviewLinearLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void configureLayout() {
        getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        getLayoutParams().width = LayoutParams.MATCH_PARENT;
    }

    public void loadReviews(Uri uri) {
        FetchReviewsTask task = new FetchReviewsTask();
        task.execute(uri);
    }

    private void populateList(Vector<ContentValues> cVVector) {
        removeAllViews();
        for (final ContentValues values: cVVector) {
            View reviewView = mLayoutInflater.inflate(R.layout.listview_item_review, this, false);

            TextView authorView = (TextView) reviewView.findViewById(R.id.text_view_author);
            authorView.setText(values.getAsString(TmdbHelper.TMDB_REVIEW_AUTHOR));

            TextView contentView = (TextView) reviewView.findViewById(R.id.text_view_content);
            contentView.setText(values.getAsString(TmdbHelper.TMDB_REVIEW_CONTENT));

            reviewView.setOnClickListener(new OnClickListener() {

                private final String reviewUri = values.getAsString(TmdbHelper.TMDB_REVIEW_URL);

                public void onClick(View v) {
                    getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUri)));
                }
            });

            addView(reviewView, -1);
        }

        if (null != mPalette) {
            applyPalette();
        }
    }

    public void setPalette(Palette palette) {
        mPalette = palette;
        applyPalette();
    }

    private void applyPalette() {
        int mutedColor = mPalette.getMutedColor(Color.GRAY);
        int mutedLightColor = mPalette.getLightMutedColor(Color.WHITE);
        int mutedDarkColor = mPalette.getDarkMutedColor(Color.BLACK);
        int darkVibrantColor = mPalette.getDarkVibrantColor(Color.BLACK);

        for (int i=0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            TextView authorView = (TextView) view.findViewById(R.id.text_view_author);
            if (null != authorView) {
                authorView.setTextColor(darkVibrantColor);
            }

            TextView contentView = (TextView) view.findViewById(R.id.text_view_content);
            if (null != contentView) {
                contentView.setTextColor(mutedDarkColor);
            }
        }
    }

    private class FetchReviewsTask extends AsyncTask<Uri, Void, Vector<ContentValues>> {
        @Override
        protected Vector<ContentValues> doInBackground(Uri... params) {
            Vector<ContentValues> cVVector =
                    TmdbHelper.parseTmdbReviewResult(TmdbHelper.fetchResultFromTmdb(params[0]));
            return cVVector;
        }

        @Override
        protected void onPostExecute(Vector<ContentValues> cVVector) {
            super.onPostExecute(cVVector);
            populateList(cVVector);
        }
    }

}
