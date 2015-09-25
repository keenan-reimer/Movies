package app.com.example.keenanreimer.movies.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
public class VideoLinearLayout extends LinearLayout {

    LayoutInflater mLayoutInflater;

    private static final int KEY_KEY = 10;

    public VideoLinearLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void configureLayout() {
        getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        getLayoutParams().width = LayoutParams.MATCH_PARENT;
    }

    public void loadVideos(Uri uri) {
        FetchVideosTask task = new FetchVideosTask();
        task.execute(uri);
    }

    private void populateList(Vector<ContentValues> cVVector) {
        removeAllViews();
        for (final ContentValues values: cVVector) {
            View videoView = mLayoutInflater.inflate(R.layout.listview_item_video, this, false);
            TextView nameView = (TextView) videoView.findViewById(R.id.textview_video_title);
            nameView.setText(values.getAsString(TmdbHelper.TMDB_VIDEO_NAME));

            videoView.setOnClickListener(new OnClickListener() {

                private final String videoKey = values.getAsString(TmdbHelper.TMDB_VIDEO_KEY);

                public void onClick(View v) {
                    getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, TmdbHelper.buildYoutubeUri(videoKey)));
                }
            });

            addView(videoView, -1);
        }

    }

    private class FetchVideosTask extends AsyncTask<Uri, Void, Vector<ContentValues>> {
        @Override
        protected Vector<ContentValues> doInBackground(Uri... params) {
            Vector<ContentValues> cVVector =
                    TmdbHelper.parseTmdbVideoResult(TmdbHelper.fetchResultFromTmdb(params[0]));
            return cVVector;
        }

        @Override
        protected void onPostExecute(Vector<ContentValues> cVVector) {
            super.onPostExecute(cVVector);
            populateList(cVVector);
        }
    }

}
