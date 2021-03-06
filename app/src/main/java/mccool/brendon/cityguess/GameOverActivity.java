package mccool.brendon.cityguess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the game over text
        Intent intent = getIntent();
        String gameOverText = intent.getStringExtra(ContinueGameActivity.GAME_OVER);
        TextView textView = (TextView) findViewById(R.id.game_over_text);
        textView.setText(gameOverText);

    }

}
