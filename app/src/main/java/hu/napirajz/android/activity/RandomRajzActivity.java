package hu.napirajz.android.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import hu.napirajz.android.Const;
import hu.napirajz.android.HeightWrapBitmapTarget;
import hu.napirajz.android.NapirajzDeserializer;
import hu.napirajz.android.OnFinishListener;
import hu.napirajz.android.R;
import hu.napirajz.android.response.NapirajzData;
import hu.napirajz.android.response.NapirajzResponse;
import hu.napirajz.android.rest.NapirajzRest;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RandomRajzActivity extends AppCompatActivity {

    public static final String NAPIRAJZ = "napirajz";
    public NapirajzRest napirajzRest;
    public Picasso picasso;
    private ImageView imageView;
    private ProgressBar progressBar;
    private FloatingActionButton nextPic;

    private NapirajzData lastNapirajzData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_rajz);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setTitle(R.string.search_pic);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder()
                                .registerTypeAdapter(NapirajzResponse.class, new NapirajzDeserializer())
                                .setDateFormat("yyyy-MM-dd")
                                .create()))
                .build();

        napirajzRest = retrofit.create(NapirajzRest.class);

        imageView = (ImageView) findViewById(R.id.napirajz_imageview);
        progressBar = (ProgressBar) findViewById(R.id.napirajz_loader);
        nextPic = (FloatingActionButton) findViewById(R.id.next_pic);

        nextPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load();
            }
        });

        if (savedInstanceState != null) {
            lastNapirajzData = (NapirajzData) savedInstanceState.getSerializable(NAPIRAJZ);
        }
        if (lastNapirajzData == null) {
            load();
        } else {
            loadPicture();
        }
    }

    private void load() {

        nextPic.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        napirajzRest.random()
                .enqueue(new Callback<NapirajzResponse>() {
                    @Override
                    public void onResponse(Call<NapirajzResponse> call, Response<NapirajzResponse> response) {
                        if (response.isSuccessful()) {
                            lastNapirajzData = response.body().getData();
                            loadPicture();
                        } else {
                            setTitle(R.string.failed);
                        }

                    }

                    @Override
                    public void onFailure(Call<NapirajzResponse> call, Throwable t) {
                        Toast.makeText(RandomRajzActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        nextPic.setEnabled(true);
                        setTitle(R.string.failed);
                    }
                });
    }

    private void loadPicture() {
        setTitle(lastNapirajzData.getCim());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.w("asd", lastNapirajzData.getUrl());
        picasso.load(lastNapirajzData.getUrl())
                .into(new HeightWrapBitmapTarget(dm.widthPixels, imageView, new OnFinishListener() {
                    @Override
                    public void success(Bitmap bitmap) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        nextPic.setEnabled(true);
//                        Toast.makeText(RandomRajzActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void error() {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        nextPic.setEnabled(true);

                        Toast.makeText(RandomRajzActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(NAPIRAJZ, lastNapirajzData);
    }

}