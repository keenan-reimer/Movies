package app.com.example.keenanreimer.movies.utility;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.Vector;

import app.com.example.keenanreimer.movies.data.MovieContract;

/**
 * Created by keenan.reimer on 9/14/2015.
 */
public class FetchDiscoveryPagesTask extends AsyncTask<Integer, Void, Void> {

    private final String LOG_TAG = FetchDiscoveryPagesTask.class.getSimpleName();

    private Context mContext;

    public FetchDiscoveryPagesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        int lastPage = params[0];
        int maxPage = params[1];
        if (maxPage < lastPage) {
            return null;
        }

        Vector<ContentValues> cVVector = new Vector<>();
        for (int i = lastPage + 1; i < maxPage + 1; i++) {
            Uri pageUri = TmdbHelper.buildPageUri(i);
            cVVector.addAll(
                    TmdbHelper.parseTmdbMovieResult(
                            TmdbHelper.fetchResultFromTmdb(pageUri)));
        }
        mContext.getContentResolver().bulkInsert(
                MovieContract.DiscoverEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
        return null;
    }
}
