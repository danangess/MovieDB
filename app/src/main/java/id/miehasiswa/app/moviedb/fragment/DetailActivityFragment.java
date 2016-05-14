package id.miehasiswa.app.moviedb.fragment;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.Utility;
import id.miehasiswa.app.moviedb.adapter.ReviewAdapter;
import id.miehasiswa.app.moviedb.adapter.VideoAdapter;
import id.miehasiswa.app.moviedb.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private View rootView;
    private MergeAdapter mergeAdapter;
    private int isFavorite;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        Uri uriMovie = intent.getData();
        View view = loadDetail(uriMovie);

        int movieID = Utility.fetchMovieIdFromUri(getActivity(), uriMovie);

        mergeAdapter = new MergeAdapter();
        mergeAdapter.addView(view);

        loadVideo(movieID);
        loadReview(movieID);

        ListView listViewDetail = (ListView) rootView.findViewById(R.id.list_detail);
        listViewDetail.setAdapter(mergeAdapter);

        return rootView;
    }

    private View loadDetail(final Uri uriMovie) {
        final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_detail, null, false);
        if (view == null) {
            return null;
        }
        final TextView textViewTitle = (TextView) view.findViewById(R.id.tVTitle);
        TextView textViewYear = (TextView) view.findViewById(R.id.tVYear);
        TextView textViewDuration = (TextView) view.findViewById(R.id.tVTime);
        TextView textViewRate = (TextView) view.findViewById(R.id.tVRate);
        TextView textViewOverview = (TextView) view.findViewById(R.id.tVOverview);
        ImageView imageViewPoster = (ImageView) view.findViewById(R.id.iVMovie);
        final Button buttonFavofite = (Button) view.findViewById(R.id.bFavorite);

        Cursor cursorDetail = getActivity().getContentResolver().query(uriMovie, null, null, null, null);

        if (cursorDetail.moveToFirst()) {
            final int id = cursorDetail.getInt(cursorDetail.getColumnIndex(MovieContract.MovieEntry._ID));

            String poster_path = cursorDetail.getString(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            Uri imageUri = Uri.parse("http://image.tmdb.org/t/p/").buildUpon()
                    .appendPath(getActivity().getString(R.string.api_image_size_default))
                    .appendPath(poster_path.substring(1))
                    .build();

            Picasso.with(getActivity())
                    .load(imageUri)
                    .placeholder(R.drawable.progress_animation)
                    .into(imageViewPoster);

            String title = cursorDetail.getString(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            textViewTitle.setText(title);
            String backdrop_path = cursorDetail.getString(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH));
            Uri uriBackdrop = Uri.parse("http://image.tmdb.org/t/p/").buildUpon()
                    .appendPath(getActivity().getString(R.string.api_image_size_medium))
                    .appendPath(backdrop_path.substring(1))
                    .build();
            Picasso.with(getActivity())
                    .load(uriBackdrop)
                    .into(new Target() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            textViewTitle.setBackground(new BitmapDrawable(bitmap));
                        }

                        @Override public void onBitmapFailed(Drawable errorDrawable) {}
                        @Override public void onPrepareLoad(Drawable placeHolderDrawable) {}
                    });

            String release_date = Utility.getReleaseYear(cursorDetail.getString(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            textViewYear.setText(release_date);

            textViewDuration.setText("- minutes");

            Double rate = cursorDetail.getDouble(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            textViewRate.setText(rate + "/10");

            String overview = cursorDetail.getString(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            textViewOverview.setText(overview);

            isFavorite = cursorDetail.getInt(cursorDetail.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));
            if (isFavorite == 1) {
                buttonFavofite.setText(R.string.details_button_favorite_remove);
            } else if (isFavorite == 0){
                buttonFavofite.setText(R.string.details_button_favorite_add);
            }

            buttonFavofite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (isFavorite) {
                        case 0:{
                            ContentValues cVFavor = new ContentValues();
                            cVFavor.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
                            int updateRows = getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, cVFavor, MovieContract.MovieEntry._ID + " = ?", new String[]{String.valueOf(id)});

                            if (updateRows <= 0) {
                                Log.d("Favorite", "Movie not marked as favorite");
                            } else {
                                Log.d("Favorite", "Movie marked as favorite");
                                buttonFavofite.setText(R.string.details_button_favorite_remove);
                                isFavorite = 1;
                            }
                            break;
                        }
                        case 1:{
                            ContentValues cVFavor = new ContentValues();
                            cVFavor.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
                            int updateRows = getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, cVFavor, MovieContract.MovieEntry._ID + " = ?", new String[]{String.valueOf(id)});

                            if (updateRows <= 0) {
                                Log.d("Favorite", "Movie not unmarked as favorite");
                            } else {
                                Log.d("Favorite", "Movie unmarked as favorite");
                                buttonFavofite.setText(R.string.details_button_favorite_add);
                                isFavorite = 0;
                            }
                            break;
                        }
                    }
                }
            });
            cursorDetail.close();
        } else {
            return null;
        }

        return view;
    }

    private void loadVideo(int movieID) {
        Cursor cursorVideo = getActivity().getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                MovieContract.VideoEntry.COLUMN_ID_MOVIE + " = ?",
                new String[]{String.valueOf(movieID)},
                null);

        VideoAdapter videoAdapter = new VideoAdapter(getActivity(), cursorVideo, 0);
        mergeAdapter.addAdapter(videoAdapter);
    }

    private void loadReview(int movieID) {
        Cursor cursorReview = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                MovieContract.ReviewEntry.COLUMN_ID_MOVIE + " = ?",
                new String[]{String.valueOf(movieID)},
                null);
        ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), cursorReview, 0);
        mergeAdapter.addAdapter(reviewAdapter);
    }
}
