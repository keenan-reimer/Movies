package app.com.example.keenanreimer.movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * Created by keenan.reimer on 9/8/2015.
 */
public class MovieProvider extends ContentProvider {
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int DISCOVER = 100;
    static final int DISCOVER_WITH_ID = 101;
    static final int FAVORITE = 102;
    static final int FAVORITE_WITH_ID = 103;
    static final int SEARCH = 104;
    static final int SEARCH_WITH_ID = 105;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteQueryBuilder;
    private static final SQLiteQueryBuilder sSearchQueryBuilder;

    //movie.movie_id = ?
    private static final String sMovieIdSelection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sFavoriteQueryBuilder = new SQLiteQueryBuilder();
        sSearchQueryBuilder = new SQLiteQueryBuilder();

        sMovieQueryBuilder.setTables(MovieContract.DiscoverEntry.TABLE_NAME);
        sFavoriteQueryBuilder.setTables(MovieContract.FavoriteEntry.TABLE_NAME);
        sSearchQueryBuilder.setTables(MovieContract.SearchEntry.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case DISCOVER:
                return MovieContract.DiscoverEntry.CONTENT_TYPE;
            case DISCOVER_WITH_ID:
                return MovieContract.DiscoverEntry.CONTENT_ITEM_TYPE;
            case FAVORITE:
                return MovieContract.FavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            case SEARCH:
                return MovieContract.SearchEntry.CONTENT_TYPE;
            case SEARCH_WITH_ID:
                return MovieContract.SearchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // When given a URI this switch statement will determine what kind of request is used,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case DISCOVER_WITH_ID:
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            case DISCOVER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.DiscoverEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_WITH_ID:
                retCursor = getFavoriteById(uri, projection, sortOrder);
                break;
            case FAVORITE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SEARCH_WITH_ID:
                retCursor = getSearchById(uri, projection, sortOrder);
                break;
            case SEARCH:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.SearchEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;

        long _id;
        switch (match) {
            case DISCOVER: {
                _id = db.insert(MovieContract.DiscoverEntry.TABLE_NAME, null, values);
                if (_id > 0) retUri = MovieContract.DiscoverEntry.buildMovieUri(
                        MovieContract.DiscoverEntry.CONTENT_URI, _id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                _id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) retUri = MovieContract.FavoriteEntry.buildMovieUri(
                        MovieContract.FavoriteEntry.CONTENT_URI, _id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SEARCH: {
                _id = db.insert(MovieContract.SearchEntry.TABLE_NAME, null, values);
                if (_id > 0) retUri = MovieContract.SearchEntry.buildMovieUri(
                        MovieContract.SearchEntry.CONTENT_URI, _id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (match) {
            case DISCOVER: {
                rowsDeleted = db.delete(MovieContract.DiscoverEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case FAVORITE: {
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SEARCH: {
                rowsDeleted = db.delete(MovieContract.SearchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsUpdated;
        switch (match) {
            case DISCOVER: {
                rowsUpdated = db.update(MovieContract.DiscoverEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVORITE: {
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case SEARCH: {
                rowsUpdated = db.update(MovieContract.SearchEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String tableName = null;
        switch (match) {
            case DISCOVER: {tableName = MovieContract.DiscoverEntry.TABLE_NAME; break;}
            case FAVORITE: {tableName = MovieContract.FavoriteEntry.TABLE_NAME; break;}
            case SEARCH: {tableName = MovieContract.SearchEntry.TABLE_NAME; break;}
        }

        if (null != tableName) {
            db.beginTransaction();
            int returnCount = 0;
            try {
                for (ContentValues value : values) {
                    long _id = db.insert(tableName, null, value);
                    if (_id > 0) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            getContext().getContentResolver().notifyChange(uri, null);
            return returnCount;
        }
        else {
            return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MovieContract.PATH_DISCOVER, DISCOVER);
        uriMatcher.addURI(authority, MovieContract.PATH_DISCOVER + "/*", DISCOVER_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        uriMatcher.addURI(authority, MovieContract.PATH_FAVORITE + "/*", FAVORITE_WITH_ID);
        uriMatcher.addURI(authority, MovieContract.PATH_SEARCH, SEARCH);
        uriMatcher.addURI(authority, MovieContract.PATH_SEARCH + "/*", SEARCH_WITH_ID);

        return uriMatcher;
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        long movieId = MovieContract.DiscoverEntry.getMovieIdFromUri(uri);
        Log.d(LOG_TAG, String.valueOf(movieId));

        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{Long.toString(movieId)};

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getFavoriteById(Uri uri, String[] projection, String sortOrder) {
        long favoriteId = MovieContract.FavoriteEntry.getMovieIdFromUri(uri);
        Log.d(LOG_TAG, String.valueOf(favoriteId));

        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{Long.toString(favoriteId)};

        return sFavoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getSearchById(Uri uri, String[] projection, String sortOrder) {
        long searchId = MovieContract.SearchEntry.getMovieIdFromUri(uri);
        Log.d(LOG_TAG, String.valueOf(searchId));

        String selection = sMovieIdSelection;
        String[] selectionArgs = new String[]{Long.toString(searchId)};

        return sSearchQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
}
