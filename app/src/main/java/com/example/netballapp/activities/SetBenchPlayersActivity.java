package com.example.netballapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.netballapp.R;

public class SetBenchPlayersActivity extends AppCompatActivity {

    Integer currentNumBenchPositions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_bench_players);

        LinearLayout benchContainer = findViewById(R.id.benchContainer);

        // Retrieve coach ID from SharedPreferences
        currentNumBenchPositions = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                .getInt("game_BenchPlayers", -1);

        if (currentNumBenchPositions == -1) {
            Toast.makeText(this, "Number of positions not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (int i = 0; i < currentNumBenchPositions; i++) {
            TextView benchLabel = new TextView(this);
            benchLabel.setText("Bench " + (i + 1));
            benchLabel.setTextSize(20); // bigger text
            benchLabel.setTextColor(Color.BLACK);
            benchLabel.setPadding(24, 24, 24, 24);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 160);
            params.setMargins(0, 16, 0, 16);
            benchLabel.setLayoutParams(params);

            benchLabel.setBackgroundResource(R.drawable.bench_label_bg);

            benchContainer.addView(benchLabel);
        }
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(SetBenchPlayersActivity.this, SetUpCourtActivity.class);
        startActivity(intent);
    }
}