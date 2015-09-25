package app.com.example.keenanreimer.movies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.com.example.keenanreimer.movies.utility.TmdbHelper;

/**
 * Created by keenan.reimer on 9/9/2015.
 */
public class MovieAdapter extends CursorAdapter {
    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.gridview_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(R.id.item_picture, view.findViewById(R.id.item_picture));
//        view.setTag(R.id.item_text, view.findViewById(R.id.item_text));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String posterPath = cursor.getString(MovieFragment.COL_MOVIE_POSTER_PATH);
        ImageView imageView = (ImageView) view.getTag(R.id.item_picture);

        Uri posterUri = TmdbHelper.buildImageUri(posterPath, 2);
        Picasso.with(context).load(posterUri).into(imageView);

//        String titleText = cursor.getString(MainFragment.COL_MOVIE_TITLE);
//        TextView textView = (TextView) view.getTag(R.id.item_text);
//        textView.setText(titleText);
    }
}
