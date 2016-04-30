package id.miehasiswa.app.moviedb;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ImageListAdapter imageListAdapter;
    private ArrayList<Movie> arrayList;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            arrayList = new ArrayList<>();
        } else {
            arrayList = savedInstanceState.getParcelableArrayList("movies");
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //ArrayList<Movie> arrayList = new ArrayList<>();
        imageListAdapter = new ImageListAdapter(getActivity(), arrayList);

        GridView gridViewMovie = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridViewMovie.setAdapter(imageListAdapter);

        gridViewMovie.setClickable(true);
        gridViewMovie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("movie", imageListAdapter.getItem(position));
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
        inflater.inflate(R.menu.movie_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = prefs.getString(getString(R.string.pref_location_key),
        //getString(R.string.pref_location_default));
        movieTask.execute("top_rated");
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
            Time time = new Time();
            time.setToNow();

            //int julianDay = Time.getJulianDay(System.currentTimeMillis(), time.gmtoff);
            //time = new Time();
            Movie[] resultMovie = new Movie[jsonArrayResults.length()];

            String image_size = "w185";
            String base_path = "http://image.tmdb.org/t/p/" + image_size;
            for (int i = 0; i < jsonArrayResults.length(); i++) {
                Movie movie = new Movie();

                JSONObject jsonObjectMovie = jsonArrayResults.getJSONObject(i);
                movie.setPosterPath(base_path + jsonObjectMovie.getString("poster_path"));
                movie.setTitle(jsonObjectMovie.getString("title"));
                movie.setOverview(jsonObjectMovie.getString("overview"));
                movie.setVoteAverage(jsonObjectMovie.getInt("vote_average"));
                movie.setReleaseDate(jsonObjectMovie.getString("release_date"));
                resultMovie[i] = movie;
            }
            return resultMovie;
        }
        @Override
        protected Movie[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonString = null;

            // Will contain the raw JSON response as a string.
            //Movie movie = null;
            String sort = params[0];

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String BASE_URL = "http://api.themoviedb.org/3/movie/" + sort + "?";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                imageListAdapter.clear();
                for(Movie image : result) {
                    imageListAdapter.add(image);
                }
            }
        }
    }
}
