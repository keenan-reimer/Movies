package app.com.example.keenanreimer.movies;

import app.com.example.keenanreimer.movies.data.MovieContract;

/**
 * Created by keenan.reimer on 9/18/2015.
 */
public class FavoriteFragment extends MovieFragment {
    public FavoriteFragment() {
        super();
        mContentUri = MovieContract.FavoriteEntry.CONTENT_URI;
    }
}
