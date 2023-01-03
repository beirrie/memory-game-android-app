package iss.workshop.ca_memorygame.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.Collections;

import iss.workshop.ca_memorygame.R;

public class GameUtils {

    public static ArrayList<Bitmap> getGridImages(Intent intent) {
        ArrayList<String> filePaths;
        filePaths = intent.getStringArrayListExtra("image_paths");

        ArrayList<Bitmap> gameImages = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePaths.get(i));
            gameImages.add(bitmap);
            gameImages.add(bitmap);
        }

        Collections.shuffle(gameImages);

        return gameImages;
    }

    public static long getDuration(String time) {
        String[] parts = time.split(":");
        long mins = Long.parseLong(parts[0].trim()) * 60000;
        long secs = Long.parseLong(parts[1].trim()) * 1000;
        long milliSecs = Long.parseLong(parts[2].trim());
        return mins + secs + milliSecs;
    }

    public static void shake(View view) {
        RotateAnimation rotate = new RotateAnimation(-1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(250);
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setInterpolator(new CycleInterpolator(3));
        view.startAnimation(rotate);
    }

    public static void pulse(View view) {
        ScaleAnimation zoom = new ScaleAnimation(1.05f, 1.05f, 1.05f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoom.setDuration(250);
        zoom.setRepeatMode(Animation.REVERSE);
        view.startAnimation(zoom);
    }

    public static void playMatchSuccessSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.match_success);
        mp.start();
    }

    public static void playMatchFailSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.match_fail);
        mp.start();
    }

    public static void playWinSound(View view) {
        MediaPlayer mp = MediaPlayer.create(view.getContext(), R.raw.win);
        mp.start();
    }
}
