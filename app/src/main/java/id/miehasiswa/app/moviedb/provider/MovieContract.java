package id.miehasiswa.app.moviedb.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by danang on 09/05/16.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "id.miehasiswa.app.moviedb.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "movie";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_FAVORITE = "favorite";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildMoviePoster(String posterUrl) {
            return CONTENT_URI.buildUpon()
                    .appendPath(posterUrl.substring(1)) //remove the heading slash
                    .build();
        }

        public static Uri buildMoviesId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getPosterUrlFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }
    }

    public static final class VideoEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "video";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static Uri buildMovieId(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "review";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ID_MOVIE = "id_movie";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getMovieIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        public static Uri buildMovieId(long insertedId) {
            return ContentUris.withAppendedId(CONTENT_URI, insertedId);
        }
    }
}
