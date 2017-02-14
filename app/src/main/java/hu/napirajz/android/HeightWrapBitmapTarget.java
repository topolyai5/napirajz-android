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
    private OnFinishListener onFinishListener;

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        float w = bitmap.getWidth();
        int newHeight;
        Bitmap scaled;
        double ratio = (double) width / w;
        newHeight = (int) (bitmap.getHeight() * ratio);
        scaled = Bitmap.createScaledBitmap(bitmap, width, newHeight, false);

        imageView.setImageBitmap(scaled);
        imageView.getLayoutParams().height = newHeight;
        imageView.getLayoutParams().width = width;

        if (onFinishListener != null) {
            onFinishListener.success(bitmap);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        if (onFinishListener != null) {
            onFinishListener.error();
        }

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
