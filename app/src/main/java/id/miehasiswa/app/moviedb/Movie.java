package id.miehasiswa.app.moviedb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by danang on 29/04/16.
 */
public class Movie implements Parcelable {
    private String poster_path      = null;
    private String overview         = null;
    private String release_date     = null;
    private Double vote_max         = 10.0;
    private String title            = null;
    private Double vote_average     = null;
    private String time             = null;

    public Movie(){}

    protected Movie(Parcel in) {
        poster_path     = in.readString();
        overview        = in.readString();
        release_date    = in.readString();
        title           = in.readString();
        vote_average    = in.readDouble();
        vote_max        = in.readDouble();
        time            = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setTitle(String title) {this.title= title;}
    public void setOverview(String overview) {this.overview = overview;}
    public void setReleaseDate(String releaseDate) {this.release_date = releaseDate;}
    public void setVoteAverage(Double vote_average) {this.vote_average = vote_average;}
    public void setPosterPath(String poster_path) {this.poster_path = poster_path;}
    public void setTime(String time) {this.time = time;}

    public String getTitle() {return this.title;}
    public String getOverview() {return this.overview;}
    public String getReleaseDate() {return this.release_date;}
    public Double getVoteAverage() {return this.vote_average;}
    public Double getVoteMax() {return this.vote_max;}
    public String getPosterPath() {return this.poster_path;}
    public String getTime() {return this.time;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(title);
        dest.writeDouble(vote_average);
        dest.writeDouble(vote_max);
        dest.writeString(time);
    }
}
