package id.miehasiswa.app.moviedb;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by danang on 29/04/16.
 */
public class ImageListAdapter extends ArrayAdapter<Movie> {

    public ImageListAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_movie, parent, false);
        }

        Movie movie = getItem(position);
        ImageView imageViewMovie = (ImageView) convertView.findViewById(R.id.list_item_movie_image);
        Picasso.with(getContext())
                .load(movie.getPosterPath())
                .placeholder(R.drawable.progress_animation)
                .into(imageViewMovie);

        return convertView;
    }
}
