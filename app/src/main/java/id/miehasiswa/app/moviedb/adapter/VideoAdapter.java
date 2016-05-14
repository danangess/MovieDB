package id.miehasiswa.app.moviedb.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import id.miehasiswa.app.moviedb.R;
import id.miehasiswa.app.moviedb.data.MovieContract;

/**
 * Created by danang on 13/05/16.
 */
public class VideoAdapter extends CursorAdapter {
    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView videoTitleTextView = (TextView) view.findViewById(R.id.video_title);
        int trailerTitleColumn = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_NAME);
        String trailerTitle = cursor.getString(trailerTitleColumn);
        videoTitleTextView.setText(trailerTitle);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int youtubeKeyColumn = cursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_KEY);
                String youtubeKey = cursor.getString(youtubeKeyColumn);
                Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=" + youtubeKey);

                Intent playTrailer = new Intent(Intent.ACTION_VIEW, videoUri);
                context.startActivity(playTrailer);
            }
        });

    }
}
