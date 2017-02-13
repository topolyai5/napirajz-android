package hu.napirajz.android;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import lombok.AllArgsConstructor;

@AllArgsConstructor(suppressConstructorProperties = true)
public class HeightWrapBitmapTarget implements Target {

    private int width;
    private ImageView imageView;

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        float w = bitmap.getWidth();

        double ratio = (double) width / w;
        int newHeight = (int) (bitmap.getHeight() * ratio);

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, width, newHeight, false);

        imageView.setImageBitmap( scaled);
        imageView.getLayoutParams().height = newHeight;
        imageView.getLayoutParams().width = width;
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
