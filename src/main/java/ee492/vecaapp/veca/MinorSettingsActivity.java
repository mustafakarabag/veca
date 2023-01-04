package ee492.vecaapp.veca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;

public class MinorSettingsActivity extends AppCompatActivity {
    SeekBar thrBar;
    SeekBar listTimeBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minor_settings);
        thrBar = (SeekBar) this.findViewById(R.id.thrBar);
        listTimeBar = (SeekBar) this.findViewById(R.id.listTimeBar);
        int thrOutput;
        int thrListTime;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = pref.edit();
        if (!pref.contains("thrOutput")){
            editor.putInt("thrOutput",20);
            editor.commit();
        }
        if (!pref.contains("thrListTime")){
            editor.putInt("thrListTime",10);
            editor.commit();
        }
        thrListTime = pref.getInt("thrListTime",10);
        thrOutput = pref.getInt("thrOutput",20);
        thrBar.setProgress(thrOutput);
        listTimeBar.setProgress(thrListTime);
        thrBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                editor.putInt("thrOutput",progress);
                editor.commit();
            }
        });
        listTimeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                editor.putInt("thrListTime",progress);
                editor.commit();
            }
        });


    }
    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(setIntent);
        finish();
    }
}
