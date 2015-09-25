package app.com.example.keenanreimer.movies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import app.com.example.keenanreimer.movies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
abstract class MovieFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MovieFragment.class.getSimpleName();

    private static final int MOVIE_LOADER = 0;
    protected boolean useUserSettings = true;
    protected boolean loaderFinished = false;

    protected MovieAdapter mMovieAdapter;
    protected GridView mGridView;

    protected Uri mContentUri = MovieContract.DiscoverEntry.CONTENT_URI;

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.DiscoverEntry._ID,
            MovieContract.DiscoverEntry.COLUMN_POSTER_PATH,
            MovieContract.DiscoverEntry.COLUMN_TITLE,
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_POSTER_PATH = 1;
    public static final int COL_MOVIE_TITLE = 2;

    public MovieFragment() {
    }

    public interface Callback {
        void onItemSelected(Uri movieUri);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.grid_movies);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    long movieId = cursor.getLong(COL_MOVIE_ID);
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MovieEntry.buildMovieUri(mContentUri, movieId));
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortMethod = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popularity_value));

        String sortOrder = MovieContract.MovieEntry.COLUMN_TIMESTAMP + " ASC";
        if (useUserSettings) {
            if (sortMethod.equals(getString(R.string.pref_sort_by_popularity_value))) {
                sortOrder = MovieContract.DiscoverEntry.COLUMN_POPULARITY + " DESC";
            } else if (sortMethod.equals(getString(R.string.pref_sort_by_rating_value))) {
                sortOrder = MovieContract.DiscoverEntry.COLUMN_VOTE_AVERAGE + " DESC";
            } else if (sortMethod.equals(getString(R.string.pref_sort_by_original_title_value))) {
                sortOrder = MovieContract.DiscoverEntry.COLUMN_ORIGINAL_TITLE + " ASC";
            } else if (sortMethod.equals(getString(R.string.pref_sort_by_release_date_value))) {
                sortOrder = MovieContract.DiscoverEntry.COLUMN_RELEASE_DATE + " DESC";
            }
        }

        return new CursorLoader(getActivity(), mContentUri,
                MOVIE_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        int selection = mGridView.getFirstVisiblePosition();
        mMovieAdapter.swapCursor(cursor);
        mGridView.setSelection(selection);
        loaderFinished = true;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
