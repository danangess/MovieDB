package id.miehasiswa.app.moviedb;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.miehasiswa.app.moviedb.data.MovieContract;
import id.miehasiswa.app.moviedb.data.MovieContract.MovieEntry;
import id.miehasiswa.app.moviedb.data.MovieContract.VideoEntry;

/**
 * Created by danang on 13/05/16.
 */
public class Utility {
    public static String getPreferredSortBy(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.prefs_sort_key), context.getString(R.string.prefs_sort_default_value));
    }

    public static String getReleaseYear(String releaseDate) {
//        String[] date = releaseDate.split("-");
//        return date[0];

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH).parse(releaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static int fetchMovieIdFromUri(Context context, Uri movieUri) {
        long _id = MovieContract.MovieEntry.getIdFromUri(movieUri);

        Cursor c = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieEntry.COLUMN_ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null);

        if (c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(MovieEntry.COLUMN_ID));
        } else {
            return -1;
        }
    }




    public static void bulkInsertMovies(Context context, String movieJsonString) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(movieJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            ArrayList<ContentValues> cvList = new ArrayList<>(jsonArrayResults.length());

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject jsonObjectMovie = jsonArrayResults.getJSONObject(i);

                ContentValues contentValuesMovie = new ContentValues();

                contentValuesMovie.put(MovieEntry.COLUMN_ID, jsonObjectMovie.getInt("id"));
                contentValuesMovie.put(MovieEntry.COLUMN_ADULT, jsonObjectMovie.getBoolean("adult"));
                contentValuesMovie.put(MovieEntry.COLUMN_FAVORITE, 0);
                contentValuesMovie.put(MovieEntry.COLUMN_OVERVIEW, jsonObjectMovie.getString("overview"));
                contentValuesMovie.put(MovieEntry.COLUMN_POPULARITY, jsonObjectMovie.getLong("popularity"));
                contentValuesMovie.put(MovieEntry.COLUMN_POSTER_PATH, jsonObjectMovie.getString("poster_path"));
                contentValuesMovie.put(MovieEntry.COLUMN_BACKDROP_PATH, jsonObjectMovie.getString("backdrop_path"));
                contentValuesMovie.put(MovieEntry.COLUMN_RELEASE_DATE, jsonObjectMovie.getString("release_date"));
                contentValuesMovie.put(MovieEntry.COLUMN_TITLE, jsonObjectMovie.getString("title"));
                contentValuesMovie.put(MovieEntry.COLUMN_VOTE_AVERAGE, jsonObjectMovie.getLong("vote_average"));
                contentValuesMovie.put(MovieEntry.COLUMN_VOTE_COUNT, jsonObjectMovie.getInt("vote_count"));

                cvList.add(contentValuesMovie);
            }

            int inserted = 0;
            // add to database
            if ( cvList.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvList.size()];
                cvList.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }
            Log.d("getMovieDataFromJson", "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("getMovieDataFromJson", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void bulkInsertVideos(Context context, String videoJsonString) throws JSONException {
        try {
            JSONObject jsonObject = new JSONObject(videoJsonString);
            JSONArray jsonArrayResults = jsonObject.getJSONArray("results");

            ArrayList<ContentValues> cvList = new ArrayList<>(jsonArrayResults.length());

            for (int i = 0; i < jsonArrayResults.length(); i++) {
                JSONObject jsonObjectVideo = jsonArrayResults.getJSONObject(i);

                ContentValues contentValuesVideo = new ContentValues();

                contentValuesVideo.put(VideoEntry.COLUMN_ID_MOVIE, jsonObject.getInt("id"));
                contentValuesVideo.put(VideoEntry.COLUMN_ID, jsonObjectVideo.getString("id"));
                contentValuesVideo.put(VideoEntry.COLUMN_KEY, jsonObjectVideo.getString("key"));
                contentValuesVideo.put(VideoEntry.COLUMN_NAME, jsonObjectVideo.getString("name"));
                contentValuesVideo.put(VideoEntry.COLUMN_SITE, jsonObjectVideo.getString("site"));
                contentValuesVideo.put(VideoEntry.COLUMN_SIZE, jsonObjectVideo.getInt("size"));
                contentValuesVideo.put(VideoEntry.COLUMN_TYPE, jsonObjectVideo.getString("type"));

                cvList.add(contentValuesVideo);
            }

            int inserted = 0;
            // add to database
            if ( cvList.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cvList.size()];
                cvList.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(VideoEntry.CONTENT_URI, cvArray);
            }
            Log.d("getVideoDataFromJson", "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e("getMovieDataFromJson", e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
