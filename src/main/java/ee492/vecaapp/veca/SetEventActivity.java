package ee492.vecaapp.veca;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static ee492.vecaapp.veca.SetEventActivity.State;

public class SetEventActivity extends AppCompatActivity implements View.OnClickListener {

    public enum State {
        INITIAL,
        DATE,
        TIME,
        TITLE,
        FINAL,
        SUCCESS, DEAD, CANCELLED, REJECTED,
        WAITING
    }

    EditText titleText, dateText, timeText;
    int eventDay, eventMonth, eventYear, eventHour, eventMinute;
    String eventTitle;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    ToggleButton typeToggle;
    CalendarElement calendarElement;
    Button act1Button;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private ImageButton mic2ButtonActive = null;
    private ImageButton mic2Button = null;
    boolean listeningSuccess;
    private SpeechAnalyst speechAnalyst;
    String resultText;
    EventStateMachine eventStateMachine = null;
    TextToSpeech t1 = null;
    private boolean mIsListening;
    State currentState = State.INITIAL;
    State oldState = State.INITIAL;
    Calendar timeCal = null;
    Calendar dateCal = null;
    View currentView = null;
    String mainCalendar = "";
    TextView extantEventsList;
    TextView systemResponseText, userResponseText;
    int typeValue = 1;
    private class EventStateMachine {
        CalendarElement calendarElement = null;
        SpeechAnalyst speechAnalyst = null;
        int count = 0;

        private EventStateMachine(CalendarElement calendarElement) {
            this.calendarElement = calendarElement;
            speechAnalyst = new SpeechAnalyst(getApplicationContext());
            count = 0;
        }

        private void setCurrentState() {
            if (!calendarElement.isTitleSetCheck()) {
                currentState = State.TITLE;
            } else if (!calendarElement.isDateSetCheck()) {
                currentState = State.DATE;
            } else if (!calendarElement.isTimeSetCheck()) {
                currentState = State.TIME;
            } else {
                currentState = State.FINAL;
            }
        }

        private void startDialogue() {
            String response = "";
            HashMap<String, String> map = new HashMap<String, String>();
            switch (currentState) {
                case DATE:
                    if (!typeToggle.isChecked()){
                        response = getApplicationContext().getResources().getString(R.string.saydateadv);
                    }else {
                        response = getApplicationContext().getResources().getString(R.string.saydateadvreminder);
                    }
                    response = response.replaceAll("#name#",eventTitle);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TIME:
                    if (!typeToggle.isChecked()){
                        response = getApplicationContext().getResources().getString(R.string.saytimeadv);
                    }else {
                        response = getApplicationContext().getResources().getString(R.string.saytimeadvreminder);
                    }

                    response = response.replaceAll("#date#",dateText.getText().toString());
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TITLE:
                    if (!typeToggle.isChecked()){
                        response = getApplicationContext().getResources().getString(R.string.saytitle);
                    }else {
                        response = getApplicationContext().getResources().getString(R.string.saytitlereminder);
                    }
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case FINAL:
                    response = "";
                    int numOfEvents = findEventsOfMoment();
                    if (numOfEvents != 0) {
                        response = getApplicationContext().getResources().getString(R.string.conflictfound);
                    }
                    if (!typeToggle.isChecked()){
                        response += getApplicationContext().getResources().getString(R.string.wanttosave);
                    }else {
                        response += getApplicationContext().getResources().getString(R.string.wanttosavereminder);
                    }

                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case SUCCESS:
                    System.out.println("type toggle checked " + typeToggle.isChecked());
                    if (!typeToggle.isChecked()){
                        if (createEvent()){
                            response = getApplicationContext().getResources().getString(R.string.eventsaved);
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                            t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                        }
                    }else {
                        if (createReminder()){
                            response = getApplicationContext().getResources().getString(R.string.eventsavedreminder);
                            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                            t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                        }
                    }

                    break;
                case REJECTED:
                    if (!typeToggle.isChecked()){
                        response = getApplicationContext().getResources().getString(R.string.wanttocancel);
                    }else {
                        response = getApplicationContext().getResources().getString(R.string.wanttocancelreminder);
                    }

                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case CANCELLED:
                    if (!typeToggle.isChecked()){
                        response = getApplicationContext().getResources().getString(R.string.eventcancelled);
                    }else {
                        response = getApplicationContext().getResources().getString(R.string.eventcancelledreminder);
                    }
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
            }
            systemResponseText.setText(response);
        }

        private void afterError() {
            notUnderstood();
        }

        private void afterResults() {
            resultText = speechAnalyst.toLowercaseTurkish(resultText);
            Calendar dateCal = null;
            Calendar timeCal = null;
            switch (currentState) {
                case TITLE:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = State.WAITING;
                        break;
                    }
                    if (calendarElement.setTitle(resultText)) {
                        count = 0;
                        setTitleText(resultText);
                        setCurrentState();
                        startDialogue();
                    } else {
                        notUnderstood();
                    }
                    break;
                case DATE:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = State.WAITING;
                        break;
                    }
                    dateCal = speechAnalyst.getDate(resultText);
                    resultText =resultText.replaceAll(speechAnalyst.lastUsedText, "");
                    timeCal = speechAnalyst.getTime(resultText);
                    if (calendarElement.setTime(timeCal)) {
                        setTimeText(timeCal);
                    }

                    if (calendarElement.setDate(dateCal)) {
                        count = 0;
                        setDateText(dateCal);

                        timeCal = speechAnalyst.getTime(resultText);

                        if (calendarElement.setTime(timeCal)) {
                            setTimeText(timeCal);
                        }
                        setCurrentState();
                        startDialogue();

                    } else if (speechAnalyst.fixTime(resultText)) {
                        count = 0;
                        timeCal = speechAnalyst.getTime(resultText);
                        calendarElement.clearTimeInfo();
                        if (calendarElement.setTime(timeCal)) {
                            setTimeText(timeCal);
                            setCurrentState();
                            startDialogue();
                        } else {
                            currentState = State.TIME;
                            startDialogue();
                        }

                    } else if (speechAnalyst.fixTitle(resultText)) {
                        calendarElement.clearTitleInfo();
                        setTitleText("");
                        currentState = State.TITLE;
                        startDialogue();
                    } else if (speechAnalyst.fixType(resultText)) {
                        typeToggle.performClick();
                    } else {
                        notUnderstood();
                    }

                    break;
                case TIME:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = State.WAITING;
                        break;
                    }
                    timeCal = speechAnalyst.getTime(resultText);
                    if (calendarElement.setTime(timeCal)) {
                        count = 0;
                        setTimeText(timeCal);
                        resultText =resultText.replaceAll(speechAnalyst.lastUsedText, "");
                        dateCal = speechAnalyst.getDate(resultText);

                        if (calendarElement.setDate(dateCal)) {
                            setDateText(dateCal);
                        }
                        setCurrentState();
                        startDialogue();
                    } else if (speechAnalyst.fixDate(resultText)) {
                        dateCal = speechAnalyst.getDate(resultText);
                        calendarElement.clearDateInfo();
                        if (calendarElement.setDate(dateCal)) {
                            setDateText(dateCal);
                            setCurrentState();
                            startDialogue();
                        } else {
                            currentState = State.DATE;
                            startDialogue();
                        }
                    } else if (speechAnalyst.fixTitle(resultText)) {
                        calendarElement.clearTitleInfo();
                        setTitleText("");
                        currentState = State.TITLE;
                        startDialogue();
                    } else if (speechAnalyst.fixType(resultText)) {
                        typeToggle.performClick();
                    } else {
                        notUnderstood();
                    }
                    break;
                case FINAL:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = State.WAITING;
                        break;
                    }
                    if (speechAnalyst.confirmationGiven(resultText)) {
                        currentState = State.SUCCESS;
                        startDialogue();
                    } else if (speechAnalyst.fixTime(resultText)) {
                        count = 0;
                        timeCal = speechAnalyst.getTime(resultText);
                        calendarElement.clearTimeInfo();
                        if (calendarElement.setTime(timeCal)) {
                            setTimeText(timeCal);
                            setCurrentState();
                            startDialogue();
                        } else {
                            currentState = State.TIME;
                            startDialogue();
                        }

                    } else if (speechAnalyst.fixDate(resultText)) {
                        dateCal = speechAnalyst.getDate(resultText);
                        calendarElement.clearDateInfo();
                        if (calendarElement.setDate(dateCal)) {
                            setDateText(dateCal);
                            setCurrentState();
                            startDialogue();
                        } else {
                            currentState = State.DATE;
                            startDialogue();
                        }
                    } else if (speechAnalyst.rejectionGiven(resultText)) {
                        currentState = State.REJECTED;
                        startDialogue();
                    } else if (speechAnalyst.fixTitle(resultText)) {
                        calendarElement.clearTitleInfo();
                        setTitleText("");
                        currentState = State.TITLE;
                        startDialogue();
                    } else if (speechAnalyst.fixType(resultText)) {
                        typeToggle.performClick();
                    } else {
                        notUnderstood();
                    }
                    break;
                case REJECTED:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = State.WAITING;
                        break;
                    }
                    if (speechAnalyst.confirmationGiven(resultText)) {
                        currentState = State.CANCELLED;
                        startDialogue();
                    } else if (speechAnalyst.rejectionGiven(resultText)) {
                        currentState = State.FINAL;
                        startDialogue();
                    }
                    break;


            }
        }

        private void notUnderstood() {
            count++;
            if (count == 3) {
                count = 0;
                return;
            }
            String response = "";
            HashMap<String, String> map = new HashMap<String, String>();
            switch (currentState) {
                case DATE:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.saydate);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TIME:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.saytime);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TITLE:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.saytitle);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case FINAL:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.wanttosave);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case REJECTED:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.wanttocancel);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
            }
            systemResponseText.setText(response);
        }

        private void afterTTS() {
            if ((currentState != SetEventActivity.State.DEAD) && (currentState != SetEventActivity.State.SUCCESS) && (currentState != SetEventActivity.State.CANCELLED)) {
                buttonMic2(currentView);
            }
            else {
                currentState = SetEventActivity.State.DEAD;
                act1Button.performClick();
            }

        }
    }

    private boolean createReminder() {
        //Creates shared preferences and initialize values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        Set<String> reminderNameSet = pref.getStringSet("reminderNames", new HashSet<String>());
        reminderNameSet.add(eventTitle);

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute);
        long startMillis = 0;
        startMillis  = beginTime.getTimeInMillis();




        editor.putStringSet("reminderNames", reminderNameSet);
        editor.putLong(eventTitle, startMillis);
        editor.commit();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_event);

        timeCal = Calendar.getInstance();
        dateCal = Calendar.getInstance();
        currentView = findViewById(android.R.id.content);
        extantEventsList = (TextView) this.findViewById(R.id.existingEvents);
        listeningSuccess = false;
        resultText = "";
        mIsListening = false;
        dateText = (EditText) findViewById(R.id.dateText);
        timeText = (EditText) findViewById(R.id.timeText);
        titleText = (EditText) findViewById(R.id.titleText);
        systemResponseText = (TextView) findViewById(R.id.systemResponseTextLabel1);
        userResponseText = (TextView) findViewById(R.id.userResponseTextLabel1);
        Calendar cal = Calendar.getInstance();
        eventDay = cal.get(Calendar.DAY_OF_MONTH);
        eventMonth = cal.get(Calendar.MONTH);
        eventYear = cal.get(Calendar.YEAR);
        eventHour = 13;
        eventMinute = 0;
        calendarElement = new CalendarElement();
        showDialogOnClicks();
        typeToggle = (ToggleButton) this.findViewById(R.id.typeToggle);
        Bundle b = getIntent().getExtras();
        typeValue = 1;
        if (b != null) {
            typeValue = b.getInt("type");
            if (typeValue == 1) {
                typeToggle.setChecked(false);
                calendarElement.setType(CalendarElement.ElementType.EVENT);
            } else if (typeValue == 2) {
                typeToggle.setChecked(true);
                calendarElement.setType(CalendarElement.ElementType.REMINDER);
            }


            if (b.getInt("hour") != -1) {
                eventHour = b.getInt("hour");
                eventMinute = b.getInt("min");
                timeCal.set(Calendar.HOUR_OF_DAY, eventHour);
                timeCal.set(Calendar.MINUTE, eventMinute);
                calendarElement.setTime(timeCal);
                setTimeText(timeCal);
            }

            if (b.getInt("day") != -1) {
                eventDay = b.getInt("day");
                eventMonth = b.getInt("month");
                eventYear = b.getInt("year");
                dateCal.set(eventYear, eventMonth, eventDay);
                calendarElement.setDate(dateCal);
                setDateText(dateCal);
            }

            if(b.getString("title").length()>3){
                eventTitle = b.getString("title");
                setTitleText(eventTitle);
                calendarElement.setTitle(eventTitle);
            }
        }


        act1Button = (Button) this.findViewById(R.id.backButton);
        act1Button.setOnClickListener(this);

        mic2ButtonActive = (ImageButton) findViewById(R.id.mic2ButtonActive);
        mic2Button = (ImageButton) findViewById(R.id.mic2Button);

        titleText = (EditText) this.findViewById(R.id.titleText);
        titleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText titleText;
                    titleText= (EditText) v;
                    hideKeyboard(v);
                    eventTitle = titleText.getText().toString();
                }

            }
        });


        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "tr");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000);


        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("tr", "TR");
                    t1.setLanguage(locale);
                    SetEventActivity.TextToSpeechListener ttslistener = new SetEventActivity.TextToSpeechListener();
                    t1.setOnUtteranceProgressListener(ttslistener);

                    eventStateMachine = new EventStateMachine(calendarElement);
                    eventStateMachine.setCurrentState();
                    eventStateMachine.startDialogue();
                }
            }
        });


        SetEventActivity.SpeechRecognitionListener listener = new SetEventActivity.SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

    }

    protected boolean createEvent() {
        long calID = 9;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(eventYear, eventMonth - 1, eventDay, eventHour + 1, eventMinute);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, eventTitle);
        values.put(CalendarContract.Events.DESCRIPTION,"");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        return true;
    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        //private static final String TAG = "";

        @Override
        public void onBeginningOfSpeech() {
            //Log.d(TAG, "onBeginingOfSpeech");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

            mic2ButtonActive.setVisibility(View.INVISIBLE);
            mic2Button.setVisibility(View.VISIBLE);
            //Log.d(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            mic2ButtonActive.setVisibility(View.INVISIBLE);
            mic2Button.setVisibility(View.VISIBLE);
            mIsListening = false;
            eventStateMachine.afterError();
            //mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            resultText = "";
            //Log.d(TAG, "error = " + error);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            //Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results) {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$
            mIsListening = false;
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            System.out.println(matches.get(0));
            resultText = matches.get(0);
            userResponseText.setText(resultText);
            eventStateMachine.afterResults();

        }
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do


        @Override
        public void onRmsChanged(float rmsdB) {
        }
    }

    protected class TextToSpeechListener extends UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            runOnUiThread(new Runnable() {
                public void run() {
                    eventStateMachine.afterTTS();
                }
            });


        }

        @Override
        public void onError(String utteranceId) {

        }
    }

    public void buttonMic2(View v) {
        /*
        if(v.getId() == R.id.micButton){

            promptSpeechOutput();
        }
        */
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(SetEventActivity.this, "Bu uygulama mikrofon kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();

        } else {
            if (!mIsListening) {
                if (currentState == State.WAITING){
                    eventStateMachine.setCurrentState();
                    eventStateMachine.startDialogue();
                }
                else{
                    mic2Button.setVisibility(View.INVISIBLE);
                    mic2ButtonActive.setVisibility(View.VISIBLE);
                    mIsListening = true;
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton) {
            Intent myIntent = new Intent(v.getContext(), ee492.vecaapp.veca.MainActivity.class);
            currentState = State.DEAD;
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            finish();

            startActivity(myIntent);
        } else if (v.getId() == R.id.typeToggle) {
            if (typeToggle.isChecked() == true) {
                typeToggle.setChecked(false);
                calendarElement.setType(CalendarElement.ElementType.EVENT);
            } else {
                typeToggle.setChecked(true);
                calendarElement.setType(CalendarElement.ElementType.REMINDER);
            }
        }
    }


    public void showDialogOnClicks() {

        dateText.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDialog(DATE_DIALOG_ID);
                    }
                }
        );

        timeText.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_DIALOG_ID);
                    }
                }
        );
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG_ID) {
            return new DatePickerDialog(this, dPickerListener, eventYear, eventMonth, eventDay);
        } else if (id == TIME_DIALOG_ID) {
            return new TimePickerDialog(this, tPickerListener, eventHour, eventMinute, true);
        } else return null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            eventDay = dayOfMonth;
            eventMonth = month + 1;
            eventYear = year;
            Toast.makeText(SetEventActivity.this, eventDay + "/" + eventMonth + "/" + eventYear, Toast.LENGTH_LONG).show();
            dateCal.set(eventYear,eventMonth - 1,eventDay);
            calendarElement.setDate(dateCal);
            setDateText(dateCal);
        }
    };

    private TimePickerDialog.OnTimeSetListener tPickerListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hour, int minute) {
            eventHour = hour;
            eventMinute = minute;
            timeCal.set(Calendar.HOUR_OF_DAY, eventHour);
            timeCal.set(Calendar.MINUTE,eventMinute);
            calendarElement.setTime(timeCal);
            Toast.makeText(SetEventActivity.this, String.format("%02d", eventHour) + ":" + String.format("%02d", eventMinute), Toast.LENGTH_LONG).show();
            setTimeText(timeCal);
        }
    };

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SetEventActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void setDateText(Calendar dateCal) {
        eventDay = dateCal.get(Calendar.DAY_OF_MONTH);
        eventMonth = dateCal.get(Calendar.MONTH) + 1;
        eventYear = dateCal.get(Calendar.YEAR);
        dateText.setText(eventDay + "/" + eventMonth + "/" + eventYear);
        findEventsOfDate();
    }

    public int findEventsOfDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(eventYear, eventMonth-1, eventDay, 0, 0, 0);
        long startDay = calendar.getTimeInMillis();
        calendar.set(eventYear, eventMonth-1, eventDay, 23, 59, 59);
        long endDay = calendar.getTimeInMillis();
        int numOfEvents = 0;
        String[] projection = new String[]{BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?";
        String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay)};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        StringBuilder extantEventsString = new StringBuilder();
        extantEventsString.append("");

        Cursor cur = getApplicationContext().getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, selection,
                        selectionArgs, null);



        try {
            numOfEvents = cur.getCount();
            if (cur.getCount() > 0) {
                extantEventsString.append(eventDay + "/" + eventMonth + "/" + eventYear + " tarihindeki etkinliker:" + "\n");
                while (cur.moveToNext()) {
                    String title = cur.getString(1);
                    Date begin = new Date(getDate(Long.parseLong(cur.getString(3))));
                    Date end = new Date(getDate(Long.parseLong(cur.getString(4))));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String beginStr = sdf.format(begin);
                    String endStr = sdf.format(end);
                    extantEventsString.append(title + System.getProperty("line.separator") + "Başlangıç Saati:" + beginStr + System.getProperty("line.separator") + "Bitiş Saati:" + endStr +"\n");
                }
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }

        extantEventsList.setText(extantEventsString.toString());

        return numOfEvents;
    }

    public int findEventsOfMoment(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(eventYear, eventMonth-1, eventDay,eventHour, eventMinute, 0);
        long momentStart = calendar.getTimeInMillis();
        calendar.set(eventYear, eventMonth-1, eventDay,eventHour+1, eventMinute, 0);
        long momentEnd = calendar.getTimeInMillis();
        int numOfEvents = 0;

        String selection = "(" + CalendarContract.Events.DTEND + " >= ? AND " + CalendarContract.Events.DTEND + "<= ?" + ")" +
                            " OR " + "(" + CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?" + ")";
        String[] selectionArgs = new String[]{Long.toString(momentStart), Long.toString(momentEnd)};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return -1;
        }
        StringBuilder extantEventsString = new StringBuilder();
        extantEventsString.append("");

        Cursor cur = getApplicationContext().getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, selection,
                        selectionArgs, null);


        try {
            numOfEvents = cur.getCount();
            if (cur.getCount() > 0) {
                extantEventsString.append("Belirtilen zamandaki etkinliker:" + "\n");
                while (cur.moveToNext()) {
                    String title = cur.getString(1);
                    Date begin = new Date(getDate(Long.parseLong(cur.getString(3))));
                    Date end = new Date(getDate(Long.parseLong(cur.getString(4))));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String beginStr = sdf.format(begin);
                    String endStr = sdf.format(end);
                    extantEventsString.append(title + System.getProperty("line.separator") + "Başlangıç Saati:" + beginStr + System.getProperty("line.separator") + "Bitiş Saati:" + endStr +"\n");
                }
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }


        extantEventsList.setText(extantEventsString.toString());

        return numOfEvents;
    }

    public void setTimeText(Calendar timeCal){
        eventHour = timeCal.get(Calendar.HOUR_OF_DAY);
        eventMinute = timeCal.get(Calendar.MINUTE);
        timeText.setText(String.format("%02d", eventHour)+ ":" + String.format("%02d", eventMinute));
        if(calendarElement.isDateSetCheck()){
            findEventsOfMoment();
        }
    }

    public void setTitleText(String title){
        eventTitle = title;
        titleText.setText(eventTitle);
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}


