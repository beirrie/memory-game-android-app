package iss.workshop.ca_memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import iss.workshop.ca_memorygame.adapter.ImageAdapter;

public class GamePage extends AppCompatActivity {

    Dialog dialog;
    EditText highScoreName;
    String nameForHighScore = "";
    List<String> scores = new ArrayList<String>();
    List<String> namesInHighScores = new ArrayList<String>();
    int newHighScoreIndex;
    TextView txtTimer;
    Handler customHandler = new Handler();
    long startTime = 0L, timeInMilliSeconds = 0L, timeSwapBuff = 0L, updateTime = 0L;

    private int numOfElements;
    private ArrayList<Bitmap> gameImageLocations = new ArrayList<>();

    private int clicked = 0;
    int lastClicked = -1;

    ImageView selectedImageView1 = null;
    ImageView selectedImageView2 = null;

    private int countMatch = 0;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {

            timeInMilliSeconds = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliSeconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs %= 60;
            int milliseconds = (int) (updateTime % 1000);
            txtTimer.setText("" + mins + ":" + String.format("%2d", secs) + ":"
                    + String.format("%3d", milliseconds));
            //this points to updateTimeThread (basically it calls itself)
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        //Start Timer
        txtTimer = findViewById(R.id.timerDynamic);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        dialog = new Dialog(this);

        ArrayList<String> filePaths = new ArrayList<>();

        Intent intent = getIntent();
        filePaths = intent.getStringArrayListExtra("image_paths");

        ArrayList<Bitmap> gameImages = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
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
                if (lastClicked == position && clicked != 0) {
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
                        TextView scoreTextView = findViewById(R.id.gameScoreDynamic);
                        scoreTextView.setText(String.valueOf(countMatch));
                        selectedImageView1.setOnClickListener(null);
                        selectedImageView2.setOnClickListener(null);
                        pulse(selectedImageView1);
                        pulse(selectedImageView2);
                        clicked = 0;
                        if (getCountMatch() == 6) {
                            customHandler.removeCallbacks(updateTimerThread);
                            playWinSound(selectedImageView2);
                            if (isHighScore()) {
                                popupEnterName();
                            } else {
                                goToImageFetchingActivity();
                            }
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

    public String getTimeScore() {
        return txtTimer.getText().toString();
    }

    public long getDuration(String time) {
        String[] parts = time.split(":");
        long mins = Long.parseLong(parts[0]) * 60000;
        long secs = Long.parseLong(parts[1]) * 1000;
        long milliSecs = Long.parseLong(parts[2]);
        return mins + secs + milliSecs;
    }

    private boolean isHighScore() {
        long scoreDuration = getDuration(getTimeScore());
        SharedPreferences pref = getSharedPreferences("scores", MODE_PRIVATE);
        String highScore0 = pref.getString("highScore0", "");
        String highScore1 = pref.getString("highScore1", "");
        String highScore2 = pref.getString("highScore2", "");
        String highScore3 = pref.getString("highScore3", "");
        String highScore4 = pref.getString("highScore4", "");
        scores.add(highScore0);
        scores.add(highScore1);
        scores.add(highScore2);
        scores.add(highScore3);
        scores.add(highScore4);
        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).isEmpty()) {
                newHighScoreIndex = i;
                return true;
            } else {
                long timeInHighscore = getDuration(scores.get(i));
                if (scoreDuration <= timeInHighscore) {
                    newHighScoreIndex = i;
                    return true;
                }
            }
        }
        return false;
    }

    private void popupEnterName() {
        dialog.setContentView(R.layout.game_won_popup);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
//                hideSoftKeyBoard();
                goToImageFetchingActivity();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.timeTaken);
        textView.setText("You took " + getTimeScore() + "ms!");
        highScoreName = dialog.findViewById(R.id.enterName);
        dialog.show();
    }

    public void enterNameHandler(View view) {
        nameForHighScore = highScoreName.getText().toString();
        if (nameForHighScore == null || nameForHighScore.isEmpty()) {
            goToImageFetchingActivity();
        }
        if (nameForHighScore.length() > 9) {
            TextView error = dialog.findViewById(R.id.errorMsg);
            error.setText("Not more than 8 letters please!");
            return;
        }
//        hideSoftKeyBoard();
        setScoreBoard();
        dialog.dismiss();
        goToImageFetchingActivity();
    }

    public void setScoreBoard() {
        SharedPreferences pref = getSharedPreferences("scores", MODE_PRIVATE);
        namesInHighScores.add(pref.getString("highScoreName0", ""));
        namesInHighScores.add(pref.getString("highScoreName1", ""));
        namesInHighScores.add(pref.getString("highScoreName2", ""));
        namesInHighScores.add(pref.getString("highScoreName3", ""));
        namesInHighScores.add(pref.getString("highScoreName4", ""));

        SharedPreferences.Editor editor = pref.edit();

        for (int i = scores.size() - 1; i > newHighScoreIndex; i--) {
            editor.putString("highScore" + i, scores.get(i - 1));
            editor.putString("highScoreName" + i, namesInHighScores.get(i - 1));
            editor.commit();
        }
        editor.putString("highScoreName" + newHighScoreIndex, nameForHighScore);
        editor.putString("highScore" + newHighScoreIndex, getTimeScore());
        editor.commit();
    }

    public void goToImageFetchingActivity() {
        Intent intent = new Intent(this, ImageFetchingActivity.class);
        startActivity(intent);
        finish();
    }

//    private void hideSoftKeyBoard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()) {
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
//    }
}