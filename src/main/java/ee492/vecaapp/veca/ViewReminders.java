package ee492.vecaapp.veca;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ViewReminders extends AppCompatActivity implements View.OnClickListener {

    TextView reminderText;
    Button deleteRemindersButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);

        reminderText = (TextView) this.findViewById(R.id.reminderListText);
        deleteRemindersButton  = (Button) this.findViewById(R.id.deleteReminders);
        deleteRemindersButton.setOnClickListener(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        Set<String> reminderNameSet = pref.getStringSet("reminderNames", new HashSet<String>());

        ArrayList<String> reminderList = new ArrayList<>();
        for(String reminderName : reminderNameSet){
            Long longdate = pref.getLong(reminderName,0);
            Date date=new Date(longdate);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String strDate = formatter.format(date);
            reminderList.add(reminderName + " \t" + strDate);
        }
        StringBuilder extantRemindersString = new StringBuilder();
        if (!reminderList.isEmpty()){
            for (String reminder: reminderList){
                extantRemindersString.append(reminder);
                extantRemindersString.append(System.getProperty("line.separator"));
            }
            reminderText.setText(extantRemindersString);
        }




    }

    public void onClick(View v){
        if (v.getId() == R.id.deleteReminders) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            Set<String> reminderNameSet = pref.getStringSet("reminderNames", new HashSet<String>());
            for(String reminderName : reminderNameSet){
                editor.remove(reminderName);
            }
            editor.putStringSet("reminderNames",new HashSet<String>() );
            editor.commit();

            onBackPressed();
        }
    }
    public void onBackPressed() {
        Intent setIntent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(setIntent);
        finish();
    }

}
