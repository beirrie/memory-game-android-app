package iss.workshop.ca_memorygame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.ca_memorygame.adapter.ImageAdapter;
import iss.workshop.ca_memorygame.utils.GameUtils;

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
            txtTimer.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            //this points to updateTimeThread (basically it calls itself)
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        dialog = new Dialog(this);

        Intent intent = getIntent();
        ArrayList<Bitmap> gameImageLocations = GameUtils.getGridImages(intent);

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
                        GameUtils.pulse(selectedImageView1);
                        GameUtils.pulse(selectedImageView2);
                        clicked = 0;
                        if (getCountMatch() == 6) {
                            customHandler.removeCallbacks(updateTimerThread);
                            GameUtils.playWinSound(selectedImageView2);
                            if (isHighScore()) {
                                popupEnterName();
                            } else {
                                Toast.makeText(GamePage.this, "Completed in " + getTimeScore() + "ms!", Toast.LENGTH_LONG).show();
                                goToImageFetchingActivity();
                            }
                        } else {
                            GameUtils.playMatchSuccessSound(selectedImageView2);
                        }
                    } else {
                        GameUtils.shake(selectedImageView1);
                        GameUtils.shake(selectedImageView2);
                        GameUtils.playMatchFailSound(selectedImageView2);
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
        //Start Timer
        txtTimer = findViewById(R.id.timerDynamic);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public int getCountMatch() {
        return countMatch;
    }

    public String getTimeScore() {
        return txtTimer.getText().toString();
    }

    private boolean isHighScore() {
        long scoreDuration = GameUtils.getDuration(getTimeScore());
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
                long timeInHighscore = GameUtils.getDuration(scores.get(i));
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
                Toast.makeText(GamePage.this, "Play again!", Toast.LENGTH_LONG).show();
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
        intent.putExtra("mode", "sp");
        startActivity(intent);
        finish();
    }
}