package hu.napirajz.android.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import hu.napirajz.android.Const;
import hu.napirajz.android.HeightWrapBitmapTarget;
import hu.napirajz.android.NapirajzDeserializer;
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

    public NapirajzRest napirajzRest;
    public Picasso picasso;
    private ImageView imageView;
    private ProgressBar progressBar;
    private FloatingActionButton nextPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_rajz);

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
        load();

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
                            NapirajzData data = response.body().getData();
                            setTitle(data.getCim());
                            loadPicture(data.getUrl());
                        }

                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        nextPic.setEnabled(true);


                    }

                    @Override
                    public void onFailure(Call<NapirajzResponse> call, Throwable t) {
                        Toast.makeText(RandomRajzActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        nextPic.setEnabled(true);
                    }
                });
    }

    private void loadPicture(String url) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        picasso.load(url)
                .into(new HeightWrapBitmapTarget(dm.widthPixels, imageView));
    }
}
