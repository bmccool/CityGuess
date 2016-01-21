package mccool.brendon.cityguess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.util.Random;

public class NewGameActivity extends AppCompatActivity {
    public final static String SECRET_CITY = "mccool.brendon.cityguess.SECRET_CITY";

    private SeekBar difficultyControl = null;
    private int maxIndex = 297;
    private int sliderIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Set initial default difficulty
        difficultyControl = (SeekBar)findViewById(R.id.difficulty_slider);
        sliderIndex = difficultyControl.getProgress();

        // Bind the difficulty slider to the slider index
        difficultyControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sliderIndex = progress;
                Log.d("SEEKBAR", "PROGRESS: " + sliderIndex);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /** Called when the user clicks the Enter button */
    public void continueGame(View view) {

        // The city is picked at random from a subset of the most populated cities list
        Random r = new Random();

        // The highest index should be the max index.  The lowest index should be 10
        // (Always include the top 10 cities, even at lowest difficulty)
        int index = ((sliderIndex * (maxIndex - 10) / 100) + 10);
        Log.d("INDEX", "Highest possible index is: " + index);
        index = r.nextInt(index);
        Log.d("INDEX", "Index is: " + index);
        String secretCity = MainActivity.getCity(index);
        secretCity = secretCity.replace(' ', '+');
        Intent intent = new Intent(this, ContinueGameActivity.class);
        intent.putExtra(SECRET_CITY, secretCity);
        startActivity(intent);
        finish();
    }

}
