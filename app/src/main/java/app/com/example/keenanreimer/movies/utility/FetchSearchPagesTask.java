package app.com.example.keenanreimer.movies.utility;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.Vector;

import app.com.example.keenanreimer.movies.data.MovieContract;

/**
 * Created by keenan.reimer on 9/16/2015.
 */
public class FetchSearchPagesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchSearchPagesTask.class.getSimpleName();

    private Context mContext;

    public FetchSearchPagesTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        String query = params[0];
        int startPage = Integer.valueOf(params[1]);
        int endPage = Integer.valueOf(params[2]);

        Vector<ContentValues> cVVector = new Vector<>();
        for (int i = startPage + 1; i < endPage + 1; i++) {
            Uri pageUri = TmdbHelper.buildSearchPageUri(query, i);
            cVVector.addAll(
                    TmdbHelper.parseTmdbMovieResult(
                            TmdbHelper.fetchResultFromTmdb(pageUri)));
        }

        mContext.getContentResolver().bulkInsert(
                MovieContract.SearchEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
        return null;
    }
}
