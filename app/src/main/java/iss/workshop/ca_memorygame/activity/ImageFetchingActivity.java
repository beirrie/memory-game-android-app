package iss.workshop.ca_memorygame.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import iss.workshop.ca_memorygame.ImageSelectListener;
import iss.workshop.ca_memorygame.R;
import iss.workshop.ca_memorygame.adapter.GridImageAdapter;
import iss.workshop.ca_memorygame.service.BgMusicService;
import iss.workshop.ca_memorygame.service.ImageFetchingService;

public class ImageFetchingActivity extends AppCompatActivity {

    ImageFetchingService imageFetchingService;
    EditText urlSearchBar;
    private BaseAdapter adapter;
    private Thread threadDownloadImage;
    private int maxImages = 20;
    private ProgressBar progressBar;
    private TextView progressDes;
    private boolean isThreadRunning;
    private ImageSelectListener listener;
    private String mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fetching);

        BgMusicService.startBgMusicService(this, "play");

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        urlSearchBar = findViewById(R.id.urlSearchBar);
        progressBar = findViewById(R.id.progressBar);
        progressDes = findViewById(R.id.progressDes);
        progressBar.setVisibility(View.INVISIBLE);
        progressDes.setText("");

        this.imageFetchingService = new ImageFetchingService();
        initializeGridView();
    }

    private void loadDefaultImage() {
        this.imageFetchingService.imageContents = new ArrayList<>();
        for (int i = 0; i < maxImages; i++) {
            this.imageFetchingService.imageContents.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.placeholder));
        }
    }

    private void initializeGridView() {
        loadDefaultImage();
        GridView gridView = findViewById(R.id.gvImages);
        adapter = new GridImageAdapter(this, this.imageFetchingService.imageContents);
        listener = new ImageSelectListener(this, mode);

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(listener);
    }

    private void prepareDataForListener() {
        listener.setFiles(imageFetchingService.imageFiles);
        List<Boolean> list = new ArrayList<>(Arrays.asList(new Boolean[imageFetchingService.imageContents.size()]));
        Collections.fill(list, Boolean.FALSE);
        listener.setSelectedImages(list);
        listener.setDownloadFinished(true);
    }

    public void fetchImageClickHandler(View view) {
        if (isThreadRunning && threadDownloadImage != null) {
            threadDownloadImage.interrupt();
            isThreadRunning = false;
        }
        imageFetchingService.imageFiles = new ArrayList<>();
        imageFetchingService.imageContents = new ArrayList<>();
        threadDownloadImage = new Thread(() -> {
            isThreadRunning = true;
            String result = imageFetchingService.prepareImageUrls(urlSearchBar.getText().toString());

            if (result == "success") {
                imageFetchingService.imageContents = new ArrayList<>();
                int count = 1;
                for (String imageUrl : imageFetchingService.imgUrlList) {
                    File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "img" + count + ".jpg");
                    imageFetchingService.downloadImage(imageUrl, tempFile);
                    runOnUiThread(new UpdateProgressRunnable(count));
                    count++;
                }
                prepareDataForListener();
                runOnUiThread(new UpdateGridViewRunnable());
            } else {
                runOnUiThread(() -> Toast.makeText(this, result, Toast.LENGTH_LONG).show());
            }
        });
        threadDownloadImage.start();
    }

    public class UpdateProgressRunnable implements Runnable {

        protected int imgIdDone;

        UpdateProgressRunnable(int idDone) {
            super();
            this.imgIdDone = idDone;
        }

        @Override
        public void run() {
            updateProgressBar(imgIdDone);
        }
    }

    public void updateProgressBar(int count) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(5 * count);
        int progress = progressBar.getProgress();

        String loadingText = "Downloading " + progress / 5 + " of 20 images";
        if (progress == 100) {
            loadingText = "Downloaded 20 of 20 images. \nSelect 6 images to start game!";
        }
        progressDes.setText(loadingText);
    }

    public class UpdateGridViewRunnable implements Runnable {

        protected int imgIdDone;

        UpdateGridViewRunnable() {
            super();
        }

        @Override
        public void run() {
            updateGridView();
        }
    }

    public void updateGridView() {
        if (this.imageFetchingService.imageContents.size() == 20) {
            GridImageAdapter fetchedImageAdapter = new GridImageAdapter(this, this.imageFetchingService.imageContents);
            GridView imageGridView = findViewById(R.id.gvImages);
            if (imageGridView != null) {
                imageGridView.setAdapter(fetchedImageAdapter);
            }
        }
    }

    @Override
    protected void onPause() {
        BgMusicService.startBgMusicService(this, "pause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        BgMusicService.startBgMusicService(this, "play");
        super.onResume();
    }
}