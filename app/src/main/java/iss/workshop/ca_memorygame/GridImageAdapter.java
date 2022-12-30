package iss.workshop.ca_memorygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

public class GridImageAdapter extends BaseAdapter {

    private final LayoutInflater inflater;

    public GridImageAdapter(Context context, ArrayList<Bitmap> fetchedImages) {
        this.fetchedImages = fetchedImages;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private final ArrayList<Bitmap> fetchedImages;

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.gridview_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.gridImage);

        imageView.setImageBitmap(fetchedImages.get(position));

        return convertView;
    }

    private Bitmap convertBitMap(File file)
    {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
}