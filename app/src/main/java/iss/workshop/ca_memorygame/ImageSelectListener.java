package iss.workshop.ca_memorygame;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import iss.workshop.ca_memorygame.activity.GameActivity;
import iss.workshop.ca_memorygame.activity.GameMultiActivity;

public class ImageSelectListener implements AdapterView.OnItemClickListener {
    private final AppCompatActivity currentActivity;
    private List<File> imageFiles;
    private List<Boolean> selectedImages;
    private boolean downloadFinished;
    private String mode;

    public ImageSelectListener(AppCompatActivity currentActivity, String mode) {
        this.currentActivity = currentActivity;
        this.downloadFinished = false;
        this.selectedImages = new ArrayList<>();
        this.mode = mode;
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

            if (mode.equalsIgnoreCase("sp")) {
                Intent intent = new Intent(this.currentActivity, GameActivity.class);
                intent.putStringArrayListExtra("image_paths", filePaths);
                this.currentActivity.startActivity(intent);
            } else if (mode.equalsIgnoreCase("mp")) {
                Intent intent = new Intent(this.currentActivity, GameMultiActivity.class);
                intent.putStringArrayListExtra("image_paths", filePaths);
                this.currentActivity.startActivity(intent);
            }
            currentActivity.finish();
        }
    }
}
