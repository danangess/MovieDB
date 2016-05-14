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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.activity.DetailActivity;
import id.miehasiswa.app.moviedb.adapter.MovieAdapter;
import id.miehasiswa.app.moviedb.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */

// TODO : update table agar table movie kolom favorite tidak ter-reset
public class FavoriteActivityFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public FavoriteActivityFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_favorite, container, false);

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie_fav);
        gridViewMovie.setAdapter(movieAdapter);

        gridViewMovie.setClickable(true);
        gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    int MOVIE_ID = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
                    Uri uriMovie = MovieContract.MovieEntry.buildMovieWithId(cursor.getInt(MOVIE_ID));

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("onCreateLoader = ", String.valueOf(id));

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH},
                MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?",
                new String[]{"1"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("onLoadFinished = ", String.valueOf(data.getCount()));
        movieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            gridViewMovie.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}