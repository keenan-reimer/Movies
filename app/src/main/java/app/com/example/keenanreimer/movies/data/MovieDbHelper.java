package app.com.example.keenanreimer.movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import app.com.example.keenanreimer.movies.data.MovieContract.*;

/**
 * Created by keenan.reimer on 9/8/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper{
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    // Any change in the database schema must be followed by an increment of this value.
    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_COLUMNS =
                DiscoverEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE, " +
                DiscoverEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                DiscoverEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_GENRE_IDS + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                DiscoverEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                DiscoverEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
                DiscoverEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                DiscoverEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, " +
                DiscoverEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL ";

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + DiscoverEntry.TABLE_NAME + " (" + SQL_COLUMNS + ");";

        final String SQL_CREATE_FAVORITE_TABLE =
                "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" + SQL_COLUMNS + ");";

        final String SQL_CREATE_SEARCH_TABLE =
                "CREATE TABLE " + SearchEntry.TABLE_NAME + " (" + SQL_COLUMNS + ");";

        Log.d(LOG_TAG, SQL_CREATE_MOVIE_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_FAVORITE_TABLE);
        Log.d(LOG_TAG, SQL_CREATE_SEARCH_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SEARCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DiscoverEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SearchEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
