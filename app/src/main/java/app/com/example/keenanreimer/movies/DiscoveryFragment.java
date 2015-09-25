package app.com.example.keenanreimer.movies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.Calendar;

import app.com.example.keenanreimer.movies.utility.FetchDiscoveryPagesTask;

/**
 * Created by keenan.reimer on 9/18/2015.
 */
public class DiscoveryFragment extends MovieFragment {
    public DiscoveryFragment() {
        super();
    }

    private static int lastLoadedPage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int moviesPerPage = getResources().getInteger(R.integer.movies_per_page);
            int pagesPerLoad = getResources().getInteger(R.integer.pages_per_load);

            int lastTotalItemCount = -1;
            long lastLoadTime = 0;
            boolean moreToLoad = true;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean loadMore = false;
                int currentPage = (firstVisibleItem + visibleItemCount) / moviesPerPage;

                // If the loader is ready, and there should be more movies to load,
                // and if you haven't initiated a load in the last second.
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (loaderFinished && moreToLoad && currentTime - lastLoadTime > 2000) {

                    // and you are closer than one page from the last loaded page,
                    if ((currentPage - lastLoadedPage) >= -1) {
                        loadMore = true;  // flag to load movies.
                    }
                    // or you are almost at the end of the gridView,
                    // (this is necessary because not all movies that are dloaded get displayed)
                    else if ((firstVisibleItem + 2 * visibleItemCount) >= totalItemCount) {

                        // check to see if any movies have been downloaded since the last attempt,
                        if (lastTotalItemCount != totalItemCount) {
                            loadMore = true;  // flag to load more movies.
                        }
                        else moreToLoad = false; // Otherwise, stop loading new movies.
                    }

                    if (loadMore) {
                        loadDiscoveryPages(lastLoadedPage, lastLoadedPage + pagesPerLoad);
                        lastLoadedPage = lastLoadedPage + pagesPerLoad;
                        lastLoadTime = currentTime;
                        lastTotalItemCount = totalItemCount;
                    }
                }
            }
        });

        return rootView;
    }

    private void loadDiscoveryPages(int lastPage, int maxPage) {
        FetchDiscoveryPagesTask moviesTask = new FetchDiscoveryPagesTask(getActivity());
        moviesTask.execute(lastPage, maxPage);
    }
}
