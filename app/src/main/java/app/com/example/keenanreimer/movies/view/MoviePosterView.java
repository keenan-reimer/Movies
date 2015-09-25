package app.com.example.keenanreimer.movies.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by keenan.reimer on 8/27/2015.
 */
public class MoviePosterView extends ImageView
{
    public MoviePosterView(Context context)
    {
        super(context);
    }

    public MoviePosterView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MoviePosterView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = (int) Math.round(1.5 * width);
        setMeasuredDimension(width, height); //Snap to width
    }
}
