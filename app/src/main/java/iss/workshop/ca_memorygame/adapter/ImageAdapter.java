package iss.workshop.ca_memorygame.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import iss.workshop.ca_memorygame.R;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    static int width;

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) view;
        }
        width = (int) Math.floor((viewGroup.getMeasuredWidth() - 40) / 3);
        imageView.setLayoutParams(new GridView.LayoutParams(width, width));

        Glide.with(imageView)
                .load(R.drawable.placeholder)
                .override(width)
                .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(mContext.getResources().getInteger(R.integer.corner_radius))))
                .into(imageView);

        return imageView;
    }
}
