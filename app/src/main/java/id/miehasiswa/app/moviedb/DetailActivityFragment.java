package id.miehasiswa.app.moviedb;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        Movie movie = intent.getParcelableExtra("movie");

        TextView textViewTitle = (TextView) rootView.findViewById(R.id.tVTitle);
        TextView textViewYear = (TextView) rootView.findViewById(R.id.tVYear);
        TextView textViewTime = (TextView) rootView.findViewById(R.id.tVTime);
        TextView textViewRate = (TextView) rootView.findViewById(R.id.tVRate);
        TextView textViewOverview = (TextView) rootView.findViewById(R.id.tVOverview);
        ImageView imageViewPoster = (ImageView) rootView.findViewById(R.id.iVMovie);

        Picasso.with(getActivity())
                .load(movie.getPosterPath())
                .into(imageViewPoster);
        textViewTitle.setText(movie.getTitle());
        textViewYear.setText(movie.getReleaseDate());
        textViewTime.setText(movie.getReleaseDate());
        textViewRate.setText(movie.getVoteAverage() + "/" + movie.getVoteMax());
        textViewOverview.setText(movie.getOverview());


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
