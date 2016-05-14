package id.miehasiswa.app.moviedb.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import id.miehasiswa.app.moviedb.data.MovieContract.MovieEntry;
import id.miehasiswa.app.moviedb.data.MovieContract.ReviewEntry;
import id.miehasiswa.app.moviedb.data.MovieContract.VideoEntry;

public class MovieProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_POSTER = 101;
    public static final int MOVIE_WITH_ID = 102;

    public static final int VIDEO = 200;
    public static final int VIDEO_WITH_MOVIE_ID = 201;

    public static final int REVIEW = 300;
    public static final int REVIEW_WITH_MOVIE_ID = 301;

    private static final SQLiteQueryBuilder sMovieByVideoAndReviewSettingQueryBuilder;

    static{
        sMovieByVideoAndReviewSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //video INNER JOIN movie ON video.movie_id = movie._id
        //review INNER JOIN movie ON review.movie_id = movie._id
        sMovieByVideoAndReviewSettingQueryBuilder.setTables(
                VideoEntry.TABLE_NAME + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + VideoEntry.TABLE_NAME +
                        "." + VideoEntry.COLUMN_ID_MOVIE +
                        " = " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID + ", " +
                ReviewEntry.TABLE_NAME + " INNER JOIN " +
                        MovieEntry.TABLE_NAME +
                        " ON " + ReviewEntry.TABLE_NAME +
                        "." + ReviewEntry.COLUMN_ID_MOVIE +
                        " = " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID
        );
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, MovieEntry.TABLE_NAME, MOVIE);
        matcher.addURI(authority, MovieEntry.TABLE_NAME + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieEntry.TABLE_NAME + "/*", MOVIE_WITH_POSTER);

        matcher.addURI(authority, VideoEntry.TABLE_NAME, VIDEO);
        matcher.addURI(authority, VideoEntry.TABLE_NAME + "/#", VIDEO_WITH_MOVIE_ID);

        matcher.addURI(authority, ReviewEntry.TABLE_NAME, REVIEW);
        matcher.addURI(authority, ReviewEntry.TABLE_NAME + "/#", REVIEW_WITH_MOVIE_ID);

        return matcher;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch(match){
            case MOVIE:
                numDeleted = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEO:
                numDeleted = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs);

                break;
            case REVIEW:
                numDeleted = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numDeleted;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_DIR_TYPE;

            case MOVIE_WITH_POSTER:
                return MovieEntry.CONTENT_ITEM_TYPE;

            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;

            case REVIEW:
                return ReviewEntry.CONTENT_DIR_TYPE;

            case REVIEW_WITH_MOVIE_ID:
                return ReviewEntry.CONTENT_ITEM_TYPE;

            case VIDEO:
                return VideoEntry.CONTENT_DIR_TYPE;

            case VIDEO_WITH_MOVIE_ID:
                return VideoEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = MovieEntry.buildMovieWithId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case VIDEO:{
                long _id = db.insert(VideoEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = VideoEntry.buildVideoWithId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case REVIEW:{
                long _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = ReviewEntry.buildVideoWithId(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry._ID + " = ?",
                        new String[] {String.valueOf(MovieEntry.getIdFromUri(uri))},
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case MOVIE_WITH_POSTER:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry.COLUMN_POSTER_PATH + " = ?",
                        new String[] {String.valueOf(MovieEntry.getPosterUrlFromUri(uri))},
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case VIDEO:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case VIDEO_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        VideoEntry.TABLE_NAME,
                        projection,
                        VideoEntry.COLUMN_ID_MOVIE + " = ?",
                        new String[]{String.valueOf(VideoEntry.getMovieIdFromUri(uri))},
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case REVIEW:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            case REVIEW_WITH_MOVIE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReviewEntry.TABLE_NAME,
                        projection,
                        ReviewEntry.COLUMN_ID_MOVIE + " = ?",
                        new String[]{String.valueOf(ReviewEntry.getMovieIdFromUri(uri))},
                        null,
                        null,
                        sortOrder);
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return retCursor;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                numUpdated = db.update(MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case VIDEO: {
                numUpdated = db.update(VideoEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case REVIEW:{
                numUpdated = db.update(ReviewEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        // keep track of successful inserts
        int numInserted = 0;
        switch(match){
            case MOVIE: {
                // allows for multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(MovieEntry.TABLE_NAME,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w("bulkInsert", "Attempting to insert " +
                                    value.getAsString(
                                            MovieEntry.COLUMN_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            }
            case VIDEO: {
                // allows for multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(VideoEntry.TABLE_NAME,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w("bulkInsert", "Attempting to insert " +
                                    value.getAsString(
                                            VideoEntry.COLUMN_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            }
            case REVIEW: {
                // allows for multiple transactions
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(ReviewEntry.TABLE_NAME,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w("bulkInsert", "Attempting to insert " +
                                    value.getAsString(
                                            ReviewEntry.COLUMN_ID)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
