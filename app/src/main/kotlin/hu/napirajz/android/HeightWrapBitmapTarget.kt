package hu.napirajz.android

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar

import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

import lombok.AllArgsConstructor

class HeightWrapBitmapTarget(var width: Int, var imageView: ImageView, var progressBar: ProgressBar) : Target {

    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
        val w = bitmap.width.toFloat()
        val newHeight: Int
        val scaled: Bitmap
        val ratio = width.toDouble() / w
        newHeight = (bitmap.height * ratio).toInt()
        scaled = Bitmap.createScaledBitmap(bitmap, width, newHeight, false)

        imageView.setImageBitmap(scaled)
        imageView.layoutParams.height = newHeight
        imageView.layoutParams.width = width

        imageView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    override fun onBitmapFailed(errorDrawable: Drawable) {
        imageView.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable) {

    }
}
