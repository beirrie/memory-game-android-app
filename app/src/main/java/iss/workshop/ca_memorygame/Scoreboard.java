package iss.workshop.ca_memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class Scoreboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        String placeholder = getResources().getString(R.string.score_blank);
        TextView top1_nameView = findViewById(R.id.top1text_name);
        TextView top2_nameView = findViewById(R.id.top2text_name);
        TextView top3_nameView = findViewById(R.id.top3text_name);
        TextView top4_nameView = findViewById(R.id.top4text_name);
        TextView top5_nameView = findViewById(R.id.top5text_name);
        TextView top1_scoreView = findViewById(R.id.top1text_time);
        TextView top2_scoreView = findViewById(R.id.top2text_time);
        TextView top3_scoreView = findViewById(R.id.top3text_time);
        TextView top4_scoreView = findViewById(R.id.top4text_time);
        TextView top5_scoreView = findViewById(R.id.top5text_time);

        SharedPreferences pref = getSharedPreferences("scores", MODE_PRIVATE);
        String top1_name = pref.getString("highScoreName0", "");
        String top2_name = pref.getString("highScoreName1", "");
        String top3_name = pref.getString("highScoreName2", "");
        String top4_name = pref.getString("highScoreName3", "");
        String top5_name = pref.getString("highScoreName4", "");
        String top1_score = pref.getString("highScore0", "");
        String top2_score = pref.getString("highScore1", "");
        String top3_score = pref.getString("highScore2", "");
        String top4_score = pref.getString("highScore3", "");
        String top5_score = pref.getString("highScore4", "");
        top1_nameView.setText(top1_name.isEmpty() ? placeholder : top1_name);
        top2_nameView.setText(top2_name.isEmpty() ? placeholder : top2_name);
        top3_nameView.setText(top3_name.isEmpty() ? placeholder : top3_name);
        top4_nameView.setText(top4_name.isEmpty() ? placeholder : top4_name);
        top5_nameView.setText(top5_name.isEmpty() ? placeholder : top5_name);
        top1_scoreView.setText(top1_score.isEmpty() ? placeholder : top1_score);
        top2_scoreView.setText(top2_score.isEmpty() ? placeholder : top2_score);
        top3_scoreView.setText(top3_score.isEmpty() ? placeholder : top3_score);
        top4_scoreView.setText(top4_score.isEmpty() ? placeholder : top4_score);
        top5_scoreView.setText(top5_score.isEmpty() ? placeholder : top5_score);
    }
}