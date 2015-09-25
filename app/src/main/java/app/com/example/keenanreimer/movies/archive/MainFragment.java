//package app.com.example.keenanreimer.movies;
//
//import android.app.Fragment;
//import android.app.LoaderManager;
//import android.content.CursorLoader;
//import android.content.Loader;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.net.Uri;
//import android.preference.PreferenceManager;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.GridView;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//
//import app.com.example.keenanreimer.movies.data.MovieContract;
//import app.com.example.keenanreimer.movies.utility.FetchDiscoveryPagesTask;
//import app.com.example.keenanreimer.movies.utility.FetchSearchPagesTask;
//
///**
// * A placeholder fragment containing a simple view.
// */
//public class MainFragment extends Fragment
//        implements LoaderManager.LoaderCallbacks<Cursor> {
//    public static final String LOG_TAG = MainFragment.class.getSimpleName();
//
//    private static final int MOVIE_LOADER = 0;
//
//    private MovieAdapter mMovieAdapter;
//    private GridView mGridView;
//
//    int lastLoadedPage = 0;
//
//    private static boolean startedOnce = false;
//    private static boolean loadable = true;
//
//    public static final int DISCOVER_STATE = 0;
//    public static final int FAVORITES_STATE = 1;
//    public static final int SEARCH_STATE = 2;
//    private int CURRENT_STATE = DISCOVER_STATE;
//    private int PREVIOUS_STATE = DISCOVER_STATE;
//    private static final ArrayList<Integer> AVAILABLE_STATES = new ArrayList<>(Arrays.asList(
//            DISCOVER_STATE,
//            FAVORITES_STATE,
//            SEARCH_STATE
//        ));
//
//    public static final String[] MOVIE_COLUMNS = {
//            MovieContract.DiscoverEntry._ID,
//            MovieContract.DiscoverEntry.COLUMN_POSTER_PATH,
//            MovieContract.DiscoverEntry.COLUMN_TITLE
//    };
//
//    public static final int COL_MOVIE_ID = 0;
//    public static final int COL_MOVIE_POSTER_PATH = 1;
//    public static final int COL_MOVIE_TITLE = 2;
//
//    public MainFragment() {
//    }
//
//    public interface Callback {
//        void onItemSelected(Uri movieUri);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
//        super.onActivityCreated(savedInstanceState);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (!startedOnce) {
//            int startPages = getResources().getInteger(R.integer.start_pages);
//            loadMovies(0, startPages);
//            startedOnce = true;
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
//
//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//
//        mGridView = (GridView) rootView.findViewById(R.id.grid_movies);
//        mGridView.setAdapter(mMovieAdapter);
//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
//                if (cursor != null) {
//                    long movieId = cursor.getLong(COL_MOVIE_ID);
//                    ((Callback) getActivity())
//                            .onItemSelected(buildStateUri(movieId));
//                }
//            }
//        });
//        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            int pagesPerLoad = getResources().getInteger(R.integer.pages_per_load);
//            int moviesPerPage = getResources().getInteger(R.integer.movies_per_page);
//
//            long lastTime = 0;
//
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                int currentPage = (firstVisibleItem + visibleItemCount) / moviesPerPage;
//                if ((currentPage - lastLoadedPage) >= -1 && loadable) {
//                    long currentTime = Calendar.getInstance().getTimeInMillis();
//                    if (currentTime - lastTime > 1000) {
//                        loadMovies(lastLoadedPage, currentPage + pagesPerLoad);
//                        lastTime = currentTime;
//                    }
//                }
//            }
//        });
//
//        return rootView;
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        String sortMethod = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popularity_value));
//
//        String sortOrder = null;
//        if (sortMethod.equals(getString(R.string.pref_sort_by_popularity_value))) {
//            sortOrder = MovieContract.DiscoverEntry.COLUMN_POPULARITY + " DESC";
//        }
//        else if (sortMethod.equals(getString(R.string.pref_sort_by_rating_value))) {
//            sortOrder = MovieContract.DiscoverEntry.COLUMN_VOTE_AVERAGE + " DESC";
//        }
//        else if (sortMethod.equals(getString(R.string.pref_sort_by_original_title_value))) {
//            sortOrder = MovieContract.DiscoverEntry.COLUMN_ORIGINAL_TITLE + " ASC";
//        }
//        else if (sortMethod.equals(getString(R.string.pref_sort_by_release_date_value))) {
//            sortOrder = MovieContract.DiscoverEntry.COLUMN_RELEASE_DATE + " DESC";
//        }
//
//        Uri moviesUri;
//        if (CURRENT_STATE == SEARCH_STATE) moviesUri = MovieContract.SearchEntry.CONTENT_URI;
//        else if (CURRENT_STATE == FAVORITES_STATE) moviesUri = MovieContract.FavoriteEntry.CONTENT_URI;
//        else moviesUri = MovieContract.DiscoverEntry.CONTENT_URI;
//
//        return new CursorLoader(getActivity(), moviesUri,
//                MOVIE_COLUMNS, null, null, sortOrder);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        int selection = mGridView.getFirstVisiblePosition();
//        mMovieAdapter.swapCursor(cursor);
//        mGridView.setSelection(selection);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mMovieAdapter.swapCursor(null);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    public void restartLoader() {
//        getLoaderManager().restartLoader(0, null, this);
//    }
//
////
////    Helper methods..
////
//
//    public boolean setState(int state) {
//        if (CURRENT_STATE == state) {
//            return true;
//        }
//
//        if (AVAILABLE_STATES.contains(state)) {
//            PREVIOUS_STATE = CURRENT_STATE;
//            CURRENT_STATE = state;
//            restartLoader();
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    public void returnToPreviousState() {
//        setState(PREVIOUS_STATE);
//    }
//
//    public int getState() {
//        return CURRENT_STATE;
//    }
//
//    private void loadMovies(int lastPage, int maxPage) {
//        FetchDiscoveryPagesTask moviesTask = new FetchDiscoveryPagesTask(getActivity());
//        moviesTask.execute(lastPage, maxPage);
//        lastLoadedPage = maxPage;
//    }
//
//    public void toggleFavorites() {
//        if (CURRENT_STATE == FAVORITES_STATE) {
//            setState(DISCOVER_STATE);
//        }
//        else {
//            setState(FAVORITES_STATE);
//        }
//    }
//
//    public void setSearchQuery(String query) {
//        FetchSearchPagesTask searchTask = new FetchSearchPagesTask(getActivity());
//        searchTask.execute(query);
//        setState(SEARCH_STATE);
//    }
//
//    public Uri buildStateUri(long movieId) {
//        if (CURRENT_STATE == FAVORITES_STATE) {
//            return MovieContract.FavoriteEntry.buildMovieUri(movieId);
//        }
//        if (CURRENT_STATE == SEARCH_STATE) {
//            return MovieContract.SearchEntry.buildMovieUri(movieId);
//        }
//        return MovieContract.DiscoverEntry.buildMovieUri(movieId);
//    }
//}
