package iss.workshop.ca_memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Intent scoreBoardIntent = new Intent(this, Scoreboard.class);
        startActivity(scoreBoardIntent);
    }
}