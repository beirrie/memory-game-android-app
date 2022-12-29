package iss.workshop.ca_memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import iss.workshop.ca_memorygame.adapter.ImageAdapter;

public class GamePage extends AppCompatActivity {

    private int numOfElements;
    private ArrayList<Integer> gameImageLocations = new ArrayList<>();

    private boolean isBusy = false;
    private int clicked = 0;
    private boolean turnOver = false;
    int lastClicked = -1;

    ImageView selectedImageView1 = null;
    ImageView selectedImageView2 = null;

    private int countMatch = 0;

    // to be replaced by fetched image url
    private final int[] gameImages = new int[]{
            R.drawable.img1, R.drawable.img2,
            R.drawable.img3, R.drawable.img4,
            R.drawable.img5, R.drawable.img6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        for (int i : gameImages) {
            gameImageLocations.add(i);
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
                if (lastClicked == position) {
                    return;
                }
                if (clicked == 0) {
                    lastClicked = position;
                    selectedImageView1 = (ImageView) view;
                    clicked++;
                    selectedImageView1.setImageResource(gameImageLocations.get(position));
                } else if (clicked == 1) {
                    selectedImageView2 = (ImageView) view;
                    clicked++;
                    selectedImageView2.setImageResource(gameImageLocations.get(position));
                    if (gameImageLocations.get(lastClicked) == gameImageLocations.get(position)) {
                        countMatch++;
                        selectedImageView1.setOnClickListener(null);
                        selectedImageView2.setOnClickListener(null);
                        clicked = 0;
                        if (getCountMatch() == 6) {
                            Toast.makeText(getApplicationContext(), "You win!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                selectedImageView1.setImageResource(R.drawable.placeholder);
                                selectedImageView2.setImageResource(R.drawable.placeholder);
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
}