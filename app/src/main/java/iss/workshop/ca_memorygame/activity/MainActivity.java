package iss.workshop.ca_memorygame.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import iss.workshop.ca_memorygame.R;
import iss.workshop.ca_memorygame.service.BgMusicService;

public class MainActivity extends AppCompatActivity {

    Intent bgMusicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BgMusicService.startBgMusicService(this, "play");
    }

    public void playClickHandlerSP(View view) {
        Intent imageFetchIntent = new Intent(this, ImageFetchingActivity.class);
        imageFetchIntent.putExtra("mode", "sp");
        startActivity(imageFetchIntent);
    }

    public void playClickHandlerMP(View view) {
        Intent imageFetchIntent = new Intent(this, ImageFetchingActivity.class);
        imageFetchIntent.putExtra("mode", "mp");
        startActivity(imageFetchIntent);
    }

    public void scoreBoardClickHandler(View view) {
        Intent scoreBoardIntent = new Intent(this, ScoreboardActivity.class);
        startActivity(scoreBoardIntent);
    }

    public void bgSoundHandler(View view) {
        ImageView soundIcon = findViewById(R.id.bgSoundIcon);
        if (BgMusicService.onBGMusic.equals("on")) {
            BgMusicService.onBGMusic = "off";
            soundIcon.setImageResource(R.drawable.music_off);
        } else {
            BgMusicService.onBGMusic = "on";
            soundIcon.setImageResource(R.drawable.music_on);
        }
        BgMusicService.startBgMusicService(this, "play");
    }

    @Override
    protected void onPause() {
        BgMusicService.startBgMusicService(this, "gaming");
        super.onPause();
    }

    @Override
    protected void onResume() {
        BgMusicService.startBgMusicService(this, "play");
        super.onResume();
    }
}