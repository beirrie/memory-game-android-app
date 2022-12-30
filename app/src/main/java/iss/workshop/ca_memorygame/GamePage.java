package iss.workshop.ca_memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.Collections;

import iss.workshop.ca_memorygame.adapter.ImageAdapter;

public class GamePage extends AppCompatActivity {

    private int numOfElements;
    private ArrayList<Bitmap> gameImageLocations = new ArrayList<>();

    private boolean isBusy = false;
    private int clicked = 0;
    private boolean turnOver = false;
    int lastClicked = -1;

    ImageView selectedImageView1 = null;
    ImageView selectedImageView2 = null;

    private int countMatch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        ArrayList<String> filePaths = new ArrayList<>();

        Intent intent = getIntent();
        filePaths = intent.getStringArrayListExtra("image_paths");

        ArrayList<Bitmap> gameImages = new ArrayList<>();

        for(int i = 0; i<6; i++){
            Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));
            gameImages.add(bitmap);
        }

        for (Bitmap bitmap : gameImages) {
            gameImageLocations.add(bitmap);
        }
        gameImageLocations.addAll(gameImageLocations);

        numOfElements = 12;

        Collections.shuffle(gameImageLocations);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        ImageAdapter imageAdapter = new ImageAdapter(this);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (lastClicked == position && clicked !=0) {
                    return;
                }
                if (clicked == 0) {
                    lastClicked = position;
                    selectedImageView1 = (ImageView) view;
                    clicked++;
                    Glide.with(selectedImageView1.getContext())
                            .load(gameImageLocations.get(position))
                            .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(getResources().getInteger(R.integer.corner_radius))))
                            .into(selectedImageView1);
                } else if (clicked == 1) {
                    selectedImageView2 = (ImageView) view;
                    clicked++;
                    Glide.with(selectedImageView2.getContext())
                            .load(gameImageLocations.get(position))
                            .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(getResources().getInteger(R.integer.corner_radius))))
                            .into(selectedImageView2);
                    if (gameImageLocations.get(lastClicked) == gameImageLocations.get(position)) {
                        countMatch++;
                        selectedImageView1.setOnClickListener(null);
                        selectedImageView2.setOnClickListener(null);
                        pulse(selectedImageView1);
                        pulse(selectedImageView2);
                        clicked = 0;
                        if (getCountMatch() == 6) {
                            Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                            playWinSound(selectedImageView2);
                        } else {
                            playMatchSuccessSound(selectedImageView2);
                        }
                    } else {
                        shake(selectedImageView1);
                        shake(selectedImageView2);
                        playMatchFailSound(selectedImageView2);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(selectedImageView1.getContext())
                                        .load(R.drawable.placeholder)
                                        .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(getResources().getInteger(R.integer.corner_radius))))
                                        .into(selectedImageView1);
                                Glide.with(selectedImageView2.getContext())
                                        .load(R.drawable.placeholder)
                                        .transform(new MultiTransformation(new CenterCrop(), new RoundedCorners(getResources().getInteger(R.integer.corner_radius))))
                                        .into(selectedImageView2);
                                clicked = 0;
                            }
                        }, 1000);
                    }
                }
            }
        });
    }

    public int getCountMatch() {
        return countMatch;
    }

    public void setCountMatch(int countMatch) {
        this.countMatch = countMatch;
    }

    private void shake(View view) {
        RotateAnimation rotate = new RotateAnimation(-1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new CycleInterpolator(3));
        view.startAnimation(rotate);
    }

    private void pulse(View view) {
        ScaleAnimation zoom = new ScaleAnimation(1.05f, 1.05f, 1.05f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoom.setDuration(250);
        zoom.setRepeatMode(Animation.REVERSE);
        view.startAnimation(zoom);
    }

    private void playMatchSuccessSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.match_success);
        mp.start();
    }

    private void playMatchFailSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.match_fail);
        mp.start();
    }

    private void playWinSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.win);
        mp.start();
    }
}