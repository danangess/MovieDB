package id.miehasiswa.app.moviedb;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by danang on 29/04/16.
 */
public class Movie implements Parcelable {
    private String poster_path = null;
    private String overview = null;
    private String release_date = null;
    private Integer vote_max = 10;
    private String title = null;
    private Integer vote_average = null;

    public Movie(Movie movie) {
        this.overview = movie.overview;
        this.release_date = movie.release_date;
        this.title = movie.title;
        this.vote_average = movie.vote_average;
        this.poster_path = movie.poster_path;
    }

    public Movie(){}

    protected Movie(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        title = in.readString();
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
    public void setVoteAverage(Integer vote_average) {this.vote_average = vote_average;}
    public void setPosterPath(String poster_path) {this.poster_path = poster_path;}

    public String getTitle() {return this.title;}
    public String getOverview() {return this.overview;}
    public String getReleaseDate() {return this.release_date;}
    public Integer getVoteAverage() {return this.vote_average;}
    public Integer getVoteMax() {return this.vote_max;}
    public String getPosterPath() {return this.poster_path;}

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
    }
}
