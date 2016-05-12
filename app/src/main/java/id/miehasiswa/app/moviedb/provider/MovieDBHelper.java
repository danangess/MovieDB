package id.miehasiswa.app.moviedb.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import id.miehasiswa.app.moviedb.data.MovieContract.VideoEntry;
import id.miehasiswa.app.moviedb.data.MovieContract.MovieEntry;
import id.miehasiswa.app.moviedb.data.MovieContract.ReviewEntry;

/**
 * Created by danang on 09/05/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    //name & version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieEntry.TABLE_NAME + "(" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_ADULT + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL), " +
                MovieEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL), " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL), " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL), " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0), " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL), " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                VideoEntry.COLUMN_ID + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_ID_MOVIE + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_KEY + "TEXT NOT NULL, " +
                VideoEntry.COLUMN_NAME + "TEXT NOT NULL, " +
                VideoEntry.COLUMN_SITE + "TEXT NOT NULL, " +
                VideoEntry.COLUMN_SIZE + "INTEGER NOT NULL, " +
                VideoEntry.COLUMN_TYPE + "TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + VideoEntry.COLUMN_ID_MOVIE + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_ID + ");";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                ReviewEntry.COLUMN_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_ID_MOVIE + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_ID_MOVIE + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_ID + ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Upgrading", "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + MovieEntry.TABLE_NAME + "'");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + VideoEntry.TABLE_NAME + "'");

        // Drop the table
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ReviewEntry.TABLE_NAME + "'");

        // re-create database
        onCreate(db);
    }
}
