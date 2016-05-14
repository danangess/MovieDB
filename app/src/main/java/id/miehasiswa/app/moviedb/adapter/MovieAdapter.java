package id.miehasiswa.app.moviedb.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.data.MovieContract.MovieEntry;

/**
 * Created by danang on 29/04/16.
 */
public class MovieAdapter extends CursorAdapter {

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageViewMovie = (ImageView) view.findViewById(R.id.list_item_movie_image);

        int moviePosterCol = cursor.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH);
        String moviePoster = cursor.getString(moviePosterCol);

        Uri imageUri = Uri.parse("http://image.tmdb.org/t/p/").buildUpon()
                .appendPath(context.getString(R.string.api_image_size_default))
                .appendPath(moviePoster.substring(1))
                .build();

        Log.d("bindView" + " - Image uri:", imageUri.toString());

        Picasso.with(context)
                .load(imageUri)
                .placeholder(R.drawable.progress_animation)
                .into(imageViewMovie);
    }
}
