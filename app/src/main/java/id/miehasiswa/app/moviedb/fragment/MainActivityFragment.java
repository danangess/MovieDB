package id.miehasiswa.app.moviedb.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import id.miehasiswa.app.moviedb.FetchMovieTask;
import id.miehasiswa.app.moviedb.adapter.MovieAdapter;
import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.Utility;
import id.miehasiswa.app.moviedb.activity.DetailActivity;
import id.miehasiswa.app.moviedb.data.MovieContract.MovieEntry;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public MainActivityFragment() {
    }

    private MovieAdapter movieAdapter;
    private GridView gridViewMovie;

    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie);
        if (gridViewMovie == null) {
            Log.d("grid", "null");
        }
        gridViewMovie.setAdapter(movieAdapter);

        gridViewMovie.setClickable(true);
        gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    int MOVIE_ID = cursor.getColumnIndex(MovieEntry._ID);
                    Uri uriMovie = MovieEntry.buildMovieWithId(cursor.getInt(MOVIE_ID));

                    intent.setData(uriMovie);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to gridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovie();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String sort = Utility.getPreferredSortBy(getActivity());
        movieTask.execute(sort.toLowerCase());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader = ", String.valueOf(id));
        String sortOrder;
        String sort = Utility.getPreferredSortBy(getActivity());

        if (sort.equals(getString(R.string.prefs_sort_default_value))) {
            sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            sortOrder = MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return new CursorLoader(getActivity(),
                MovieEntry.CONTENT_URI,
                new String[]{MovieEntry._ID, MovieEntry.COLUMN_POSTER_PATH},
                null,
                null,
                sortOrder + " LIMIT 20");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished = ", String.valueOf(data.getCount()));
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridViewMovie.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
