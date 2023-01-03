package iss.workshop.ca_memorygame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import iss.workshop.ca_memorygame.adapter.ImageAdapter;
import iss.workshop.ca_memorygame.utils.GameUtils;

public class GameMulti extends AppCompatActivity {

    Dialog dialog;
    int player = 1;
    List<Long> time = new ArrayList<Long>(Arrays.asList(0L, 0L));
    ArrayList<Bitmap> gameImageLocations;
    TextView txtTimer;
    Handler customHandler = new Handler();
    long startTime = 0L, timeInMilliSeconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    String p1Time;

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
        gameImageLocations = GameUtils.getGridImages(intent);

        startGame(gameImageLocations);
    }

    private void startGame(ArrayList<Bitmap> gameImageLocations) {
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
                            if (player == 1) {
                                time.set(0, timeInMilliSeconds);
                                popupNextPlayer();
                            } else {
                                time.set(1, timeInMilliSeconds);
                                popupEndGame();
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

    private void popupNextPlayer() {
        countMatch = 0;
        dialog.setContentView(R.layout.next_player_popup);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                startGame(gameImageLocations);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        p1Time = "Player 1 took " + getTimeScore() + " seconds";
        dialog.show();
    }

    private void popupEndGame() {
        dialog.setContentView(R.layout.mp_winner_popup);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                goToImageFetchingActivity();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView textView = dialog.findViewById(R.id.timeTaken);
        String p2Time = "Player 2 took " + getTimeScore() + " seconds";
        String displayString = p1Time + "\n" + p2Time;
        textView.setText(displayString);
        TextView winner = dialog.findViewById(R.id.winnerId);
        winner.setText(getWinner());
        dialog.show();
    }

    private String getWinner() {

        String winner = "";

        if (time.get(0) < time.get(1)) {
            winner = "Player 1";
        } else if (time.get(0) > time.get(1)) {
            winner = "Player 2";
        } else if (time.get(0) == time.get(1)) {
            winner = "None";
        }

        return winner;
    }

    public void nextPlayer(View view) {
        player++;
        dialog.dismiss();
    }

    public void goToImageFetchingActivity() {
        Intent intent = new Intent(this, ImageFetchingActivity.class);
        intent.putExtra("mode", "mp");
        startActivity(intent);
        finish();
    }
}
