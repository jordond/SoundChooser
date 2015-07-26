package ca.hoogit.sample;

import android.content.DialogInterface;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Field;

import ca.hoogit.soundchooser.SoundChooserDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);
        mTextView = (TextView) findViewById(R.id.chosen);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int[] soundFiles = getSoundsList();

        SoundChooserDialog dialog = SoundChooserDialog.newInstance("Choose a sound",
                soundFiles, Color.parseColor("#2196F3"), 5, SoundChooserDialog.SIZE_SMALL);

        //New feature:
//        dialog.setTheme(R.style.DialogThemeSample);
        //

        dialog.setAudioStreamType(AudioManager.STREAM_ALARM);
        dialog.setOnOptionChosen(new SoundChooserDialog.OnOptionChosen() {
            @Override
            public void onSoundSelected(int soundId) {

            }

            @Override
            public void onPositive(DialogInterface dialog, int soundId) {
                mTextView.setText("ID of chosen sound: " + soundId);
                dialog.dismiss();
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                mTextView.setText("Dialog was cancelled");
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "soundChooser");
    }

    public int[] getSoundsList() {
        Field[] fields = R.raw.class.getFields();
        int[] sounds = new int[fields.length];
        try {
            for (int count = 0; count < fields.length; count++) {
                sounds[count] = fields[count].getInt(fields[count]);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sounds;
    }
}
