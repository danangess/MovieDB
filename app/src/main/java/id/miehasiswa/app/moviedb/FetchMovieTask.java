package id.miehasiswa.app.moviedb;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

import id.miehasiswa.app.moviedb.data.MovieContract;

/**
 * Created by danang on 12/05/16.
 */

public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private final Context context;

    public FetchMovieTask(Context context) {
        this.context = context;
    }

    private Void setMovieDataFromJson(String movieJsonString) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(movieJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            ArrayList<ContentValues> cvList = new ArrayList<>(jsonArrayResults.length());

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject jsonObjectMovie = jsonArrayResults.getJSONObject(i);

                ContentValues contentValuesMovie = new ContentValues();

                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_ID, jsonObjectMovie.getInt("id"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_ADULT, jsonObjectMovie.getBoolean("adult"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, jsonObjectMovie.getString("overview"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_POPULARITY, jsonObjectMovie.getDouble("popularity"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, jsonObjectMovie.getString("poster_path"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, jsonObjectMovie.getString("backdrop_path"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, jsonObjectMovie.getString("release_date"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_TITLE, jsonObjectMovie.getString("title"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, jsonObjectMovie.getDouble("vote_average"));
                contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, jsonObjectMovie.getInt("vote_count"));

                cvList.add(contentValuesMovie);
                setVideoDataFromJson(getVideoJsonString(String.valueOf(jsonObjectMovie.getInt("id"))));
                setReviewDataFromJson(getReviewJsonString(String.valueOf(jsonObjectMovie.getInt("id"))));
            }

            int inserted = 0;
            // add to database
            if ( cvList.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvList.size()];
                cvList.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }
            Log.d("setMovieDataFromJson", "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("setMovieDataFromJson", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Void setVideoDataFromJson(String videoJsonString) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(videoJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            ArrayList<ContentValues> cvList = new ArrayList<>(jsonArrayResults.length());

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject jsonObjectVideo = jsonArrayResults.getJSONObject(i);

                ContentValues contentValuesVideo = new ContentValues();

                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_ID_MOVIE, jsonObject.getInt("id"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_ID, jsonObjectVideo.getString("id"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_KEY, jsonObjectVideo.getString("key"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_NAME, jsonObjectVideo.getString("name"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_SITE, jsonObjectVideo.getString("site"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_SIZE, jsonObjectVideo.getInt("size"));
                contentValuesVideo.put(MovieContract.VideoEntry.COLUMN_TYPE, jsonObjectVideo.getString("type"));

                cvList.add(contentValuesVideo);
            }

            int inserted = 0;
            // add to database
            if ( cvList.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvList.size()];
                cvList.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
            }
            Log.d("setVideoDataFromJson", "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("setVideoDataFromJson", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private Void setReviewDataFromJson(String videoJsonString) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(videoJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            ArrayList<ContentValues> cvList = new ArrayList<>(jsonArrayResults.length());

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject jsonObjectReview = jsonArrayResults.getJSONObject(i);

                ContentValues contentValuesReview = new ContentValues();

                contentValuesReview.put(MovieContract.ReviewEntry.COLUMN_ID_MOVIE, jsonObject.getInt("id"));
                contentValuesReview.put(MovieContract.ReviewEntry.COLUMN_ID, jsonObjectReview.getString("id"));
                contentValuesReview.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, jsonObjectReview.getString("author"));
                contentValuesReview.put(MovieContract.ReviewEntry.COLUMN_CONTENT, jsonObjectReview.getString("content"));
                contentValuesReview.put(MovieContract.ReviewEntry.COLUMN_URL, jsonObjectReview.getString("url"));

                cvList.add(contentValuesReview);
            }

            int inserted = 0;
            // add to database
            if ( cvList.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvList.size()];
                cvList.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }
            Log.d("setReviewDataFromJson", "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("setReviewDataFromJson", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String getMovieJsonString(String sort) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonString;

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
//            getMovieDataFromJson(movieJsonString);
        } catch (IOException e) {
            Log.e("doInBackground", "Error ", e);
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
        return movieJsonString;
    }

    private String getVideoJsonString(String movie_id) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String videoJsonString;

        try {
            String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos?";
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
            videoJsonString = buffer.toString();
//            getMovieDataFromJson(movieJsonString);
        } catch (IOException e) {
            Log.e("doInBackground", "Error ", e);
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
        return videoJsonString;
    }

    private String getReviewJsonString(String movie_id) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String reviewJsonString;

        try {
            String BASE_URL = "http://api.themoviedb.org/3/movie/" + movie_id + "/reviews?";
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
            reviewJsonString = buffer.toString();
//            getMovieDataFromJson(movieJsonString);
        } catch (IOException e) {
            Log.e("doInBackground", "Error ", e);
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
        return reviewJsonString;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        try {
//            setMovieDataFromJson(getMovieJsonString(params[0]));
            setMovieDataFromJson(getMovieJsonString(params[0]));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}