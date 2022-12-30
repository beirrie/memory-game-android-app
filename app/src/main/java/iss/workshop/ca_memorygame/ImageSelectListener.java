package iss.workshop.ca_memorygame;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageSelectListener implements AdapterView.OnItemClickListener {
    private final AppCompatActivity currentActivity;
    private List<File> imageFiles;
    private List<Boolean> selectedImages;
    private boolean downloadFinished;

    public ImageSelectListener(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
        this.downloadFinished = false;
        this.selectedImages = new ArrayList<>();
    }

    public void setFiles(List<File> files) {
        this.imageFiles = files;
    }

    public void setSelectedImages(List<Boolean> selectedFlags) {
        this.selectedImages = selectedFlags;
    }

    public void setDownloadFinished(boolean downloadFinished) {
        this.downloadFinished = downloadFinished;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!downloadFinished) {
            return;
        }

        GridView gridView = (GridView) adapterView;
        ViewGroup gridElement = (ViewGroup) gridView.getChildAt(position);

        Boolean selected = !selectedImages.get(position);
        selectedImages.set(position, selected);

        ImageView tickBox = gridElement.findViewById(R.id.tickBox);
        if (selected) {
            tickBox.setVisibility(View.VISIBLE);
        } else {
            tickBox.setVisibility(View.INVISIBLE);
        }

        int numOfSelected = 0;

        for(int count=0; count < selectedImages.size(); count++)
        {
            if(selectedImages.get(count))
            {
                numOfSelected++;
            }
        }

        if (numOfSelected == 6) {
            ArrayList<String> filePaths = new ArrayList<>();

            for(int count=0; count < selectedImages.size(); count++)
            {
                if(selectedImages.get(count))
                {
                    filePaths.add(imageFiles.get(count).getAbsolutePath());
                }
            }

            Intent intent = new Intent(this.currentActivity, GamePage.class);
            intent.putStringArrayListExtra("image_paths", filePaths);
            this.currentActivity.startActivity(intent);
        }
    }
}
