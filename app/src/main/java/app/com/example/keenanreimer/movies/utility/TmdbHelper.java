package app.com.example.keenanreimer.movies.utility;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import app.com.example.keenanreimer.movies.data.MovieContract;

/**
 * Created by keenan.reimer on 9/14/2015.
 */
public class TmdbHelper {
    private static final String LOG_TAG = TmdbHelper.class.getSimpleName();
    
    private static final String apiKey = "3130beddeb60d78134b5d6757b01cc3b";
    private static final Uri baseImageUri = Uri.parse("http://image.tmdb.org/t/p");
    private static final Uri baseApiUri = Uri.parse("http://api.themoviedb.org/3");
    private static final Uri baseMovieUri = baseApiUri.buildUpon().appendEncodedPath("movie").build();
    private static final Uri baseDiscoverMovieUri = baseApiUri.buildUpon().appendEncodedPath("discover/movie").build();
    private static final Uri baseSearchUri  = baseApiUri.buildUpon().appendEncodedPath("search/movie").build();

    private static final Uri baseYoutubeUri = Uri.parse("http://www.youtube.com/watch");
    private static final String YOUTUBE_VIDEO = "v";

//  TMDB query parameters.
    private static final String TMDB_KEY_PARAM = "api_key";
    private static final String TMDB_SORT_PARAM = "sort_by";
    private static final String TMDB_PAGE_PARAM = "page";
    private static final String TMDB_VOTE_COUNT_PARAM = "vote_count.gte";
    private static final String TMDB_VOTE_AVERAGE_PARAM = "vote_average";
    private static final String TMDB_QUERY_PARAM = "query";
    private static final String TMDB_SEARCH_BY_PARAM = "search_type";

//  TMDB query values.
    private static final String TMDB_SORT_BY_POPULARITY = "popularity.desc";
    private static final String TMDB_SEARCH_BY_AUTOCOMPLETE = "ngram";
    private static final String TMDB_SEARCH_BY_PHRASE = "phrase";

//  Movie result keys.
    private static final String TMDB_ADULT = "adult";
    private static final String TMDB_BACKDROP_PATH = "backdrop_path";
    private static final String TMDB_GENRE_IDS = "genre_ids";
    private static final String TMDB_ID = "id";
    private static final String TMDB_ORIGINAL_LANGUAGE = "original_language";
    private static final String TMDB_ORIGINAL_TITLE = "original_title";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_RELEASE_DATE = "release_date";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_POPULARITY = "popularity";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_VIDEO = "video";
    private static final String TMDB_VOTE_AVERAGE = "vote_average";
    private static final String TMDB_VOTE_COUNT = "vote_count";
    private static final String TMDB_RESULTS = "results";

    //    Video result keys.
    public static final String TMDB_VIDEO_ID = "id";
    public static final String TMDB_VIDEO_ISO_639_1 = "iso_639_1";
    public static final String TMDB_VIDEO_KEY = "key";
    public static final String TMDB_VIDEO_NAME = "name";
    public static final String TMDB_VIDEO_SITE = "site";
    public static final String TMDB_VIDEO_SIZE = "size";
    public static final String TMDB_VIDEO_TYPE = "type";

    // Review result keys.
    public static final String TMDB_REVIEW_ID = "id";
    public static final String TMDB_REVIEW_AUTHOR = "author";
    public static final String TMDB_REVIEW_CONTENT = "content";
    public static final String TMDB_REVIEW_URL = "url";

    private static final String[] sizes = {"w92", "w154", "w185", "w342", "w500", "w780", "original"};
    private static String mDateFormat = "yyyy-MM-dd";

    public static Uri buildImageUri(String imageSubUrl, int sizeIndex) {
        return baseImageUri.buildUpon()
                .appendEncodedPath(sizes[sizeIndex])
                .appendEncodedPath(imageSubUrl)
                .build();
    }

    public static Uri buildPageUri(int page) {
        return baseDiscoverMovieUri.buildUpon()
                .appendQueryParameter(TMDB_PAGE_PARAM, (String.valueOf(page)))
                .appendQueryParameter(TMDB_SORT_PARAM, TMDB_SORT_BY_POPULARITY)
                .appendQueryParameter(TMDB_VOTE_COUNT_PARAM, "10")
                .appendQueryParameter(TMDB_KEY_PARAM, apiKey)
                .build();
    }

    public static Uri buildSearchPageUri(String query, int page) {
        return baseSearchUri.buildUpon()
                .appendQueryParameter(TMDB_QUERY_PARAM, query)
                .appendQueryParameter(TMDB_KEY_PARAM, apiKey)
                .appendQueryParameter(TMDB_SEARCH_BY_PARAM, TMDB_SEARCH_BY_AUTOCOMPLETE)
                .appendQueryParameter(TMDB_PAGE_PARAM, String.valueOf(page))
                .build();
    }

    public static Uri buildVideoUri(long id) {
        return baseMovieUri.buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath("videos")
                .appendQueryParameter(TMDB_KEY_PARAM, apiKey)
                .build();
    }

    public static Uri buildReviewUri(long id) {
        return baseMovieUri.buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .appendEncodedPath("reviews")
                .appendQueryParameter(TMDB_KEY_PARAM, apiKey)
                .build();
    }

    public static Uri buildYoutubeUri(String videoKey) {
        return baseYoutubeUri.buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO, videoKey).build();
    }

    public static Calendar parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(mDateFormat);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(dateString));
            return cal;
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Vector<ContentValues> parseTmdbMovieResult(String moviesJsonStr) {
        try {
            JSONObject tmdbPage = new JSONObject(moviesJsonStr);
            JSONArray jsonMoviesArray = tmdbPage.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(jsonMoviesArray.length());

            for (int i = 0; i < jsonMoviesArray.length(); i++) {
                JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.DiscoverEntry.COLUMN_ADULT,
                        jsonMovie.getBoolean(TMDB_ADULT) ? 1 : 0);
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_BACKDROP_PATH,
                        jsonMovie.getString(TMDB_BACKDROP_PATH));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_GENRE_IDS,
                        jsonMovie.getJSONArray(TMDB_GENRE_IDS).toString());
                movieValues.put(MovieContract.DiscoverEntry._ID,
                        jsonMovie.getLong(TMDB_ID));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_ORIGINAL_LANGUAGE,
                        jsonMovie.getString(TMDB_ORIGINAL_LANGUAGE));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_ORIGINAL_TITLE,
                        jsonMovie.getString(TMDB_ORIGINAL_TITLE));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_OVERVIEW,
                        jsonMovie.getString(TMDB_OVERVIEW));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_RELEASE_DATE,
                        jsonMovie.getString(TMDB_RELEASE_DATE));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_POSTER_PATH,
                        jsonMovie.getString(TMDB_POSTER_PATH));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_POPULARITY,
                        jsonMovie.getDouble(TMDB_POPULARITY));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_TITLE,
                        jsonMovie.getString(TMDB_TITLE));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_VIDEO,
                        jsonMovie.getBoolean(TMDB_VIDEO) ? 1 : 0);
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_VOTE_AVERAGE,
                        jsonMovie.getDouble(TMDB_VOTE_AVERAGE));
                movieValues.put(MovieContract.DiscoverEntry.COLUMN_VOTE_COUNT,
                        jsonMovie.getLong(TMDB_VOTE_COUNT));
                movieValues.put(MovieContract.MovieEntry.COLUMN_TIMESTAMP,
                        Calendar.getInstance().getTimeInMillis());

                if (validateTmdbMovieContents(movieValues)) cVVector.add(movieValues);
            }

            return cVVector;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Vector<>();
    }

    public static boolean validateTmdbMovieContents(ContentValues videoValues) {
        if ("null".equals(videoValues.getAsString(TMDB_TITLE))) return false;
        if ("null".equals(videoValues.getAsString(TMDB_OVERVIEW))) return false;
        if ("null".equals(videoValues.getAsString(TMDB_POSTER_PATH))) return false;
        return true;
    }


    public static Vector<ContentValues> parseTmdbVideoResult(String videoResultStr) {
        try {
            JSONObject result = new JSONObject(videoResultStr);
            JSONArray videoResults = result.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(videoResults.length());

            for (int i =0; i < videoResults.length(); i++) {
                JSONObject videoObject = videoResults.getJSONObject(i);

                ContentValues values = new ContentValues();

                values.put(TMDB_VIDEO_ID,
                        videoObject.getString(TMDB_VIDEO_ID));
                values.put(TMDB_VIDEO_ISO_639_1,
                        videoObject.getString(TMDB_VIDEO_ISO_639_1));
                values.put(TMDB_VIDEO_KEY,
                        videoObject.getString(TMDB_VIDEO_KEY));
                values.put(TMDB_VIDEO_NAME,
                        videoObject.getString(TMDB_VIDEO_NAME));
                values.put(TMDB_VIDEO_SITE,
                        videoObject.getString(TMDB_VIDEO_SITE));
                values.put(TMDB_VIDEO_SIZE,
                        videoObject.getInt(TMDB_VIDEO_SIZE));
                values.put(TMDB_VIDEO_TYPE,
                        videoObject.getString(TMDB_VIDEO_TYPE));

                cVVector.add(values);
            }

            return cVVector;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return new Vector<>();
    }

    public static Vector<ContentValues> parseTmdbReviewResult(String reviewResultStr) {
        try {
            JSONObject result = new JSONObject(reviewResultStr);
            JSONArray reviewResults = result.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<>(reviewResults.length());

            for (int i =0; i < reviewResults.length(); i++) {
                JSONObject reviewObject = reviewResults.getJSONObject(i);

                ContentValues values = new ContentValues();

                values.put(TMDB_REVIEW_ID,
                        reviewObject.getString(TMDB_REVIEW_ID));
                values.put(TMDB_REVIEW_AUTHOR,
                        reviewObject.getString(TMDB_REVIEW_AUTHOR));
                values.put(TMDB_REVIEW_CONTENT,
                        reviewObject.getString(TMDB_REVIEW_CONTENT));
                values.put(TMDB_REVIEW_URL,
                        reviewObject.getString(TMDB_REVIEW_URL));

                cVVector.add(values);
            }

            return cVVector;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return new Vector<>();
    }

    public static String fetchResultFromTmdb(Uri uri) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;
        try {
            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return "";
            }
            moviesJsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e("ForecastFragment", "Error", e);
            return "";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        Log.d(LOG_TAG, uri.toString());
        return moviesJsonStr;
//        getContentResolver().bulkInsert(
//                MovieContract.DiscoverEntry.CONTENT_URI, cVVector.toArray(new ContentValues[cVVector.size()]));
    }
}
