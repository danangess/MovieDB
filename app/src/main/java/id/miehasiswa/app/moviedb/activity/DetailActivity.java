package id.miehasiswa.app.moviedb.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.fragment.DetailActivityFragment;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_detail, new DetailActivityFragment())
                    .commit();
        }
    }
}
