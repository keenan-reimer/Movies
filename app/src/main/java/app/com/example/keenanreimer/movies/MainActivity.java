package app.com.example.keenanreimer.movies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

public class MainActivity extends Activity implements MovieFragment.Callback {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private SharedPreferences mPrefs;
    private SearchView mSearchView;
    private String mSortMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            enterDiscoverState(false);
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSortMethod = mPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popularity_value));
    }

    @Override
    public void onResume() {
        super.onResume();

        String sortMethod = mPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popularity_value));
        if (mSortMethod != null && !mSortMethod.equals(sortMethod)) {
            MovieFragment activeFragment =
                    (MovieFragment) getFragmentManager().findFragmentById(R.id.container_main);
            if (null != activeFragment) {
                activeFragment.restartLoader();
                mSortMethod = sortMethod;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Fragment activeFragment = getFragmentManager().findFragmentById(R.id.container_main);
            if (activeFragment instanceof SearchFragment) {
                SearchFragment searchFragment = (SearchFragment) activeFragment;
                String query = intent.getStringExtra(SearchManager.QUERY);
                searchFragment.setSearchQuery(query);
                mSearchView.clearFocus();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView =
                (SearchView) searchMenuItem.getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())
        );

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onBackPressed();
                searchMenuItem.collapseActionView();  // Switch search back to icon mode.
                mSearchView.clearFocus(); // Drop the keyboard.
                invalidateOptionsMenu();  // Reload the menu.
                return true;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSearchState();
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment activeFragment = getFragmentManager().findFragmentById(R.id.container_main);
        menu.findItem(R.id.action_favorites).setIcon(
                activeFragment instanceof FavoriteFragment ?
                        R.drawable.checked : R.drawable.unchecked);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_favorites) {
            Fragment activeFragment = getFragmentManager().findFragmentById(R.id.container_main);
            if (activeFragment instanceof FavoriteFragment) {
                onBackPressed();
            }
            else {
                enterFavoritesState();
            }
        }
        if (id == R.id.action_search) {
            Fragment activeFragment = getFragmentManager().findFragmentById(R.id.container_main);
            if (!(activeFragment instanceof SearchFragment)) {
                enterSearchState();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        invalidateOptionsMenu();
    }

    private void enterDiscoverState(boolean addToBackStack) {
        FragmentManager fMan = getFragmentManager();
        FragmentTransaction fTran = fMan.beginTransaction()
                .replace(R.id.container_main, new DiscoveryFragment());
        if (addToBackStack) {
            fTran.addToBackStack(null);
        }
        fTran.commit();
        fMan.executePendingTransactions();
        invalidateOptionsMenu();
    }

    private void enterFavoritesState() {
        FragmentManager fMan = getFragmentManager();
        fMan.beginTransaction()
                .replace(R.id.container_main, new FavoriteFragment())
                .addToBackStack(null)
                .commit();
        fMan.executePendingTransactions();
        invalidateOptionsMenu();
    }

    private void enterSearchState() {
        FragmentManager fMan = getFragmentManager();
        fMan.beginTransaction()
                .replace(R.id.container_main, new SearchFragment())
                .addToBackStack(null)
                .commit();
    }

//    private void returnToActiveState() {
//        FragmentManager fMan = getFragmentManager();
//        fMan.beginTransaction()
//                .replace(R.id.container_main, mActiveFragment)
//                .commit();
//        fMan.executePendingTransactions();
//    }

//    Call back methods!

    @Override
    public void onItemSelected(Uri movieUri) {
        View containerDetail = this.findViewById(R.id.container_detail);
        if (null == containerDetail) {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
        else {
                Bundle args = new Bundle();
                args.putParcelable(DetailFragment.DETAIL_URI, movieUri);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(args);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container_detail, fragment)
                        .commit();
        }
    }
}
