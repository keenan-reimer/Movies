package app.com.example.keenanreimer.movies;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

import app.com.example.keenanreimer.movies.data.MovieContract;
import app.com.example.keenanreimer.movies.utility.PaletteTransformation;
import app.com.example.keenanreimer.movies.utility.TmdbHelper;
import app.com.example.keenanreimer.movies.view.ReviewLinearLayout;
import app.com.example.keenanreimer.movies.view.VideoLinearLayout;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static int DETAIL_LOADER = 1;
    static final String DETAIL_URI = "URI";

    private Uri mUri;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.DiscoverEntry._ID,
            MovieContract.DiscoverEntry.COLUMN_ADULT,
            MovieContract.DiscoverEntry.COLUMN_BACKDROP_PATH,
            MovieContract.DiscoverEntry.COLUMN_GENRE_IDS,
            MovieContract.DiscoverEntry.COLUMN_ORIGINAL_LANGUAGE,
            MovieContract.DiscoverEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.DiscoverEntry.COLUMN_OVERVIEW,
            MovieContract.DiscoverEntry.COLUMN_RELEASE_DATE,
            MovieContract.DiscoverEntry.COLUMN_POSTER_PATH,
            MovieContract.DiscoverEntry.COLUMN_POPULARITY,
            MovieContract.DiscoverEntry.COLUMN_TITLE,
            MovieContract.DiscoverEntry.COLUMN_VIDEO,
            MovieContract.DiscoverEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.DiscoverEntry.COLUMN_VOTE_COUNT
    };

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_ADULT = 1;
    private static final int COL_MOVIE_BACKDROP_PATH = 2;
    private static final int COL_MOVIE_GENRE_IDS = 3;
    private static final int COL_MOVIE_ORIGINAL_LANGUAGE = 4;
    private static final int COL_MOVIE_ORIGINAL_TITLE = 5;
    private static final int COL_MOVIE_OVERVIEW = 6;
    private static final int COL_MOVIE_RELEASE_DATE = 7;
    private static final int COL_MOVIE_POSTER_PATH = 8;
    private static final int COL_MOVIE_POPULARITY = 9;
    private static final int COL_MOVIE_TITLE = 10;
    private static final int COL_MOVIE_VIDEO = 11;
    private static final int COL_MOVIE_VOTE_AVERAGE = 12;
    private static final int COL_MOVIE_VOTE_COUNT = 13;

    private FrameLayout mBackgroundView;
    private TextView mTitleView;
    private ImageView mPosterView;
    private TextView mDateView;
    private TextView mVotingView;
    private TextView mAdultView;
    private TextView mOverviewView;
    private CheckBox mCheckboxView;
    private View mVideoSpace;
    private VideoLinearLayout mVideoView;
    private View mReviewSpace;
    private ReviewLinearLayout mReviewView;

    private int mId = -1;
    private int mAdult;
    private String mBackdropPath;
    private String mGenreIds;
    private String mOriginalLanguage;
    private String mOrginialTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mPosterPath;
    private double mPopularity;
    private String mTitle;
    private int mVideo;
    private double mVoteAverage;
    private long mVoteCount;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mUri = args.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mBackgroundView = (FrameLayout) rootView.findViewById(R.id.layout_detail_primary);
        mTitleView = (TextView) rootView.findViewById(R.id.text_view_title);
        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        mDateView = (TextView) rootView.findViewById(R.id.date_text_view);
        mVotingView = (TextView) rootView.findViewById(R.id.rating_text_view);
        mAdultView = (TextView) rootView.findViewById(R.id.adult_text_view);
        mOverviewView = (TextView) rootView.findViewById(R.id.overview_text_view);

        // Programatically create and add the custom videos view.
        mVideoView = new VideoLinearLayout(getActivity());
        mVideoSpace = rootView.findViewById(R.id.space_videos);
        ViewGroup spacerVideosParent = (ViewGroup) mVideoSpace.getParent();
        int spacerVideoIndex = spacerVideosParent.indexOfChild(mVideoSpace);
        spacerVideosParent.addView(mVideoView, spacerVideoIndex + 1);
        mVideoView.configureLayout();

        // Programatically create and add the custom reviews view.
        mReviewView = new ReviewLinearLayout(getActivity());
        mReviewSpace = rootView.findViewById(R.id.spacer_reviews);
        ViewGroup spacerReviewsParent = (ViewGroup) mReviewSpace.getParent();
        int spacerReviewIndex = spacerReviewsParent.indexOfChild(mReviewSpace);
        spacerReviewsParent.addView(mReviewView, spacerReviewIndex + 1);
        mReviewView.configureLayout();

        mCheckboxView = (CheckBox) rootView.findViewById(R.id.check_box_favorite);
        mCheckboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFavoriteClicked();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            return new CursorLoader(getActivity(), mUri,
                    DETAIL_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }
        mId = cursor.getInt(COL_MOVIE_ID);
        mVideoView.loadVideos(TmdbHelper.buildVideoUri(mId));
        mReviewView.loadReviews(TmdbHelper.buildReviewUri(mId));

        mAdult = cursor.getInt(COL_MOVIE_ADULT);
        mBackdropPath = cursor.getString(COL_MOVIE_BACKDROP_PATH);
        mGenreIds = cursor.getString(COL_MOVIE_GENRE_IDS);
        mOriginalLanguage = cursor.getString(COL_MOVIE_ORIGINAL_LANGUAGE);
        mOrginialTitle = cursor.getString(COL_MOVIE_ORIGINAL_TITLE);
        mPopularity = cursor.getDouble(COL_MOVIE_POPULARITY);
        mVideo = cursor.getInt(COL_MOVIE_VIDEO);

        mTitle = cursor.getString(COL_MOVIE_TITLE);
        mTitleView.setText(mTitle);

        mOverview = cursor.getString(COL_MOVIE_OVERVIEW);
        mOverviewView.setText(mOverview);

        mReleaseDate = cursor.getString(COL_MOVIE_RELEASE_DATE);
        Calendar cal = TmdbHelper.parseDate(mReleaseDate);
        mDateView.setText(
                getString(R.string.format_release_date,
                        cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase(),
                        String.valueOf(cal.get(Calendar.YEAR)))
        );

        mPosterPath = cursor.getString(COL_MOVIE_POSTER_PATH);
        Uri imageUri = TmdbHelper.buildImageUri(mPosterPath, 2);
        Picasso.with(getActivity()).load(imageUri)
                .transform(PaletteTransformation.instance())
                .into(mPosterView, new Callback.EmptyCallback() {
                    @Override public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) mPosterView.getDrawable()).getBitmap();
                        Palette palette = PaletteTransformation.getPalette(bitmap);
                        int vibrantLightColor = palette.getLightVibrantColor(Color.WHITE);
                        int vibrantDarkColor = palette.getDarkVibrantColor(Color.BLACK);
                        int vibrantColor = palette.getVibrantColor(Color.WHITE);
                        int mutedColor = palette.getMutedColor(Color.GRAY);
                        int mutedLightColor = palette.getLightMutedColor(Color.WHITE);
                        int mutedDarkColor = palette.getDarkMutedColor(Color.BLACK);

                        int fadeDuration = getResources().getInteger(R.integer.fade_duration);
                        ObjectAnimator.ofObject(
                                mTitleView,
                                "textColor",
                                new ArgbEvaluator(),
                                Color.WHITE,
                                vibrantLightColor
                        ).setDuration(fadeDuration).start();

                        ObjectAnimator.ofObject(
                                mTitleView,
                                "backgroundColor",
                                new ArgbEvaluator(),
                                Color.BLACK,
                                vibrantDarkColor
                        ).setDuration(fadeDuration).start();

                        ObjectAnimator.ofObject(
                                mVideoSpace,
                                "backgroundColor",
                                new ArgbEvaluator(),
                                Color.GRAY,
                                mutedColor
                        ).setDuration(fadeDuration).start();

                        ObjectAnimator.ofObject(
                                mReviewSpace,
                                "backgroundColor",
                                new ArgbEvaluator(),
                                Color.GRAY,
                                mutedColor
                        ).setDuration(fadeDuration).start();

                        ObjectAnimator.ofObject(
                                mBackgroundView,
                                "backgroundColor",
                                new ArgbEvaluator(),
                                Color.WHITE,
                                mutedLightColor
                        ).setDuration(fadeDuration).start();

                        mOverviewView.setTextColor(mutedDarkColor);
                        mVotingView.setTextColor(mutedDarkColor);
                        mDateView.setTextColor(mutedColor);
                        mAdultView.setTextColor(vibrantColor);

                        mReviewView.setPalette(palette);
                    }
                });

        mVoteAverage = cursor.getDouble(COL_MOVIE_VOTE_AVERAGE);
        mVoteCount = cursor.getLong(COL_MOVIE_VOTE_COUNT);
        String formatVoting = getString(R.string.format_voting);
        mVotingView.setText(String.format(formatVoting, mVoteAverage, mVoteCount));

//        if (mAdult == 1) {
//            mAdultView.setText("ADULT");
//        }

        Uri favUri = MovieContract.FavoriteEntry.buildMovieUri(MovieContract.FavoriteEntry.CONTENT_URI, mId);
        Cursor favCursor = getActivity().getContentResolver().query(favUri, null, null, null, null);
        if (favCursor.moveToFirst()) mCheckboxView.setChecked(true);
        else mCheckboxView.setChecked(false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onFavoriteClicked() {
        if (null == mCheckboxView) {
            return;
        }
        boolean checked = mCheckboxView.isChecked();
        if ( mId < 0 ) {
            mCheckboxView.setChecked(!checked);
            return;
        }

        if (checked) {
            ContentValues values = new ContentValues();

            values.put(MovieContract.DiscoverEntry._ID, mId);
            values.put(MovieContract.DiscoverEntry.COLUMN_ADULT, mAdult);
            values.put(MovieContract.DiscoverEntry.COLUMN_BACKDROP_PATH, mBackdropPath);
            values.put(MovieContract.DiscoverEntry.COLUMN_GENRE_IDS, mGenreIds);
            values.put(MovieContract.DiscoverEntry.COLUMN_ORIGINAL_LANGUAGE, mOriginalLanguage);
            values.put(MovieContract.DiscoverEntry.COLUMN_ORIGINAL_TITLE, mOrginialTitle);
            values.put(MovieContract.DiscoverEntry.COLUMN_OVERVIEW, mOverview);
            values.put(MovieContract.DiscoverEntry.COLUMN_RELEASE_DATE, mReleaseDate);
            values.put(MovieContract.DiscoverEntry.COLUMN_POSTER_PATH, mPosterPath);
            values.put(MovieContract.DiscoverEntry.COLUMN_POPULARITY, mPopularity);
            values.put(MovieContract.DiscoverEntry.COLUMN_TITLE, mTitle);
            values.put(MovieContract.DiscoverEntry.COLUMN_VIDEO, mVideo);
            values.put(MovieContract.DiscoverEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
            values.put(MovieContract.DiscoverEntry.COLUMN_VOTE_COUNT, mVoteCount);
            values.put(MovieContract.MovieEntry.COLUMN_TIMESTAMP,
                    Calendar.getInstance().getTimeInMillis());

            Uri uri = getActivity().getContentResolver().insert(MovieContract.FavoriteEntry.CONTENT_URI, values);
            Log.d(LOG_TAG, "Inserted: " + uri.toString());

            Toast.makeText(getActivity(), mTitle + " added to favorites.", Toast.LENGTH_SHORT).show();
        }
        else {
            String selection = MovieContract.DiscoverEntry._ID + " = ?";
            String[] selectionArgs = new String[] {String.valueOf(mId)};
            int deleted = getActivity().getContentResolver().delete(
                    MovieContract.FavoriteEntry.CONTENT_URI, selection, selectionArgs);
            Log.d(LOG_TAG, "Deleted: " + String.valueOf(deleted));
        }
    }
}
