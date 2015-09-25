package app.com.example.keenanreimer.movies.view;

import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * Created by keenan.reimer on 9/10/2015.
 */
public class MovieScrollView extends GridView {
    public MovieScrollView(Context context) {
        super(context);
    }

    public interface Callback {
        void onScrollBottomReached();
    }

    @Override
    protected void onScrollChanged(int l, int t, int old, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);

        int diff = view.getBottom() - (getHeight() + getScrollY());

        if( diff < 10 ) {
            ((Callback) getContext()).onScrollBottomReached();
        }

        super.onScrollChanged(l, t, old, oldt);
    }
}
