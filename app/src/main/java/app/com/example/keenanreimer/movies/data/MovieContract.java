package app.com.example.keenanreimer.movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by keenan.reimer on 9/8/2015.
 */
public class MovieContract {

    // Unique name for entire content provider.
    public static final String CONTENT_AUTHORITY = "com.example.keenanreimer.movies";

    // The base URI for which all apps will use to communicate with the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths within the content provider.
    public static final String PATH_DISCOVER = "discover";
    public static final String PATH_FAVORITE = "favorite";
    public static final String PATH_SEARCH = "search";

    // Class that defines the table contents of the movie table.
    public static final class DiscoverEntry extends MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "discover";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DISCOVER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISCOVER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISCOVER;
    }

    // Class that defines the table contents of the movie table.
    public static final class FavoriteEntry extends MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorite";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
    }

    // Class that defines the table contents of the movie table.
    public static final class SearchEntry extends MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "search";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH;
    }

    // Class that defines the table contents of the favorites table.

    public static class MovieEntry {

        // Movie id as returned by API
        public static final String COLUMN_MOVIE_ID = "_id";

        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_GENRE_IDS = "genre_ids";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "original_language";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

        public static final String COLUMN_TIMESTAMP = "timestamp";

        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildMovieUri(Uri contentUri, long id) {
            return ContentUris.withAppendedId(contentUri, id);
        }
    }
}
