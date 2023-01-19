package iss.workshop.ca_memorygame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import iss.workshop.ca_memorygame.R;

public class ImageUtils {
    public static void setImage(ImageView imageView, Bitmap bitmap) {
        Context context = imageView.getContext();
        Glide.with(context)
                .load(bitmap)
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(context.getResources().getInteger(R.integer.corner_radius))))
                .into(imageView);
    }

    public static void setImage(ImageView imageView, int drawableInt) {
        Context context = imageView.getContext();
        Drawable drawable = context.getDrawable(drawableInt);
        Glide.with(context)
                .load(drawable)
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(context.getResources().getInteger(R.integer.corner_radius))))
                .into(imageView);
    }
}
