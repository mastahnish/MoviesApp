package pl.myosolutions.musicapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.myosolutions.musicapp.db.ListInfoDataSource;
import pl.myosolutions.musicapp.db.ListInfoRepository;
import pl.myosolutions.musicapp.db.MovieDatabase;
import pl.myosolutions.musicapp.db.MovieRepository;
import pl.myosolutions.musicapp.db.MoviesDataSource;
import pl.myosolutions.musicapp.http.MovieResponse;
import pl.myosolutions.musicapp.http.MoviesAPI;
import pl.myosolutions.musicapp.model.ListInfo;
import pl.myosolutions.musicapp.model.Movie;
import pl.myosolutions.musicapp.view.main.list.ILoadMore;

public class MainViewModel extends AndroidViewModel implements ILoadMore {

    public LiveData<List<Movie>> movies;
    private Context context;
    private MovieRepository movieRepository;
    private ListInfoRepository listInfoRepository;
    private MoviesAPI.MovieService movieApiService;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    public MainViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        MovieDatabase db = MovieDatabase.Companion.getInstance(context);
        movieRepository = MovieRepository.Companion.getInstance(MoviesDataSource.Companion.getInstance(db.movieDAO()));
        listInfoRepository = ListInfoRepository.Companion.getInstance(ListInfoDataSource.Companion.getInstance(db.listInfoDAO()));
        movieApiService = MoviesAPI.MovieService.Companion.create();

        movies = movieRepository.getAllMovies();
    }




    @Override
    public void onLoadMore() {

    }

    public void loadMovies() {
        loadFromExternalServer(1);
    }

    private void loadFromExternalServer(int page) {

        Disposable disposable = movieApiService.getMovies(MoviesAPI.API_KEY, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(

                        result -> {
                            insertToDB(result);
                        },

                        error -> {
                            Log.d("MoviesApp", error.getMessage());
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                );

        compositeDisposable.add(disposable);


    }


    private void insertToDB(MovieResponse response) {
        Log.d("MoviesApp", new ListInfo(1, response.getPage(), response.getTotal_results(), response.getTotal_pages()).toString());

        if (response != null) {
            insertInfo(new ListInfo(1, response.getPage(), response.getTotal_results(), response.getTotal_pages()));
            insertMovies(response.getResults());
        }

    }

    private void insertInfo(ListInfo listInfo) {
        listInfoRepository.insertInfo(listInfo);
    }

    private void insertMovies(List<Movie> movies) {
        movieRepository.insertAll(movies);

    }

}
