package id.miehasiswa.app.moviedb.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import id.miehasiswa.app.moviedb.BuildConfig;
import id.miehasiswa.app.moviedb.MovieAdapter;
import id.miehasiswa.app.moviedb.Movie;
import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.activity.DetailActivity;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> arrayList;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            arrayList = new ArrayList<>();
        } else {
            arrayList = savedInstanceState.getParcelableArrayList("movies");
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), arrayList);

        GridView gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridViewMovie.setAdapter(movieAdapter);

        gridViewMovie.setClickable(true);
        gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", movieAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", arrayList);
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
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sharedPreferences.getString(getString(R.string.prefs_sort_key), getString(R.string.prefs_sort_default_value));
        movieTask.execute(sort.toLowerCase());
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private Movie[] getMovieDataFromJson(String movieJsonString) throws JSONException {
            JSONObject jsonObject = new JSONObject(movieJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            Movie[] resultMovie = new Movie[jsonArrayResults.length()];

            String image_size = "w185";
            String base_path = "http://image.tmdb.org/t/p/" + image_size;

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                Movie movie = new Movie();

                JSONObject jsonObjectMovie = jsonArrayResults.getJSONObject(i);

                movie.setPosterPath(base_path + jsonObjectMovie.getString("poster_path"));
                movie.setTitle(jsonObjectMovie.getString("title"));
                movie.setOverview(jsonObjectMovie.getString("overview"));
                movie.setVoteAverage(jsonObjectMovie.getDouble("vote_average"));
                movie.setReleaseDate(jsonObjectMovie.getString("release_date"));
                movie.setTime(jsonObjectMovie.getString("release_date"));

                resultMovie[i] = movie;
            }
            return resultMovie;
        }

        @Override
        protected Movie[] doInBackground(String... params) {
            String sort = params[0];
            if (params.length == 0) {
                sort = "popular";
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonString = null;

            try {
                String BASE_URL = "http://api.themoviedb.org/3/movie/" + sort + "?";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to TheMovieDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonString = buffer.toString();
            } catch (IOException e) {
                Log.e("Log", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Log", "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(movieJsonString);
            }catch (Exception e){
                Log.e("Log", "Error get movies", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                movieAdapter.clear();
                for(Movie image : result) {
                    movieAdapter.add(image);
                }
            }
        }
    }
}
