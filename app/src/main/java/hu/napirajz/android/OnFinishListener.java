package hu.napirajz.android;

import android.graphics.Bitmap;

public interface OnFinishListener {

    void success(Bitmap bitmap);

    void error();

}
