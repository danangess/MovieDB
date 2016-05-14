package id.miehasiswa.app.moviedb.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.fragment.FavoriteActivityFragment;

public class FavoriteActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_fav, new FavoriteActivityFragment())
                    .commit();
        }
        getSupportActionBar().setElevation(0f);
    }
}
