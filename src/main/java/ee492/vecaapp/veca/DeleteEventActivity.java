package ee492.vecaapp.veca;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DeleteEventActivity extends AppCompatActivity implements View.OnClickListener {

    //States for the conversation machine
    public enum State {
        INITIAL, // Initial state
        TITLE, //Get event from the title
        TITLENOTFOUND, //Could not find an event with the title
        EVENTSELECTION, //Select event among more than one
        EVENTSELECTED, //Event is determined
        SUCCESS, DEAD, CANCELLED, REJECTED, // Result states
        WAITING //Hold on state
    }
    //Objects from layout
    EditText titleText, dateText, timeText;
    TextView systemResponseText, userResponseText;
    Button act1Button;
    private ImageButton mic2ButtonActive = null;
    private ImageButton mic2Button = null;

    //Info of the event
    int eventDay, eventMonth, eventYear, eventHour, eventMinute;
    String eventTitle;
    //IDs for pickers
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    CalendarElement calendarElement;

    //Speech recognizer
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIsListening;
    //TTS object
    TextToSpeech t1 = null;

    private SpeechAnalyst speechAnalystDelete;
    boolean listeningSuccess;
    String resultText;

    //State machine for conversation
    DeleteEventActivity.DeleteStateMachine deleteStateMachine = null;
    DeleteEventActivity.State currentState = DeleteEventActivity.State.INITIAL;
    DeleteEventActivity.State oldState = DeleteEventActivity.State.INITIAL;

    Calendar timeCal = null;
    Calendar dateCal = null;
    View currentView = null;
    ArrayList<ArrayList<String>> extantEventsList;
    TextView extantEventsText;
    ArrayList<String> eventsOfTitleList;
    ArrayList<String> eventsOfTitleIDList;
    ArrayList<String> eventsOfTitleSTList;
    ArrayList<String> eventsOfTitleETList;
    String selectedEventID;
    String selectedEventTitle;
    String selectedEventStartTime;
    String selectedEventEndTime;


    private class DeleteStateMachine {

        CalendarElement calendarElement = null;
        SpeechAnalyst speechAnalyst = null;
        int count = 0;
        boolean eventSelected = false;
        //Constructor
        private DeleteStateMachine(CalendarElement calendarElement) {
            this.calendarElement = calendarElement;
            speechAnalyst = new SpeechAnalyst(getApplicationContext());
            count = 0;
        }
        //To determine the state
        private void setCurrentState() {
            if (!eventSelected) {
                currentState = DeleteEventActivity.State.TITLE;
            } else {
                currentState = State.EVENTSELECTED;
            }
        }

        //According to the current state designs a response and gives it to TTS
        private void startDialogue() {
            String response;
            response ="";
            HashMap<String, String> map = new HashMap<String, String>();
            switch (currentState) {
                case TITLE:
                    response = getApplicationContext().getResources().getString(R.string.saytitleofevent);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TITLENOTFOUND:
                    response = getApplicationContext().getResources().getString(R.string.titlenotfound) + getApplicationContext().getResources().getString(R.string.saytitleofevent);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    currentState = DeleteEventActivity.State.TITLE;
                    break;
                case EVENTSELECTION:
                    if (eventsOfTitleList.size() == 2) {
                        response = getApplicationContext().getResources().getString(R.string.twoeventsfound);
                        response = response.replaceAll("#event1#", eventsOfTitleList.get(0));
                        response = response.replaceAll("#event2#", eventsOfTitleList.get(1));
                    } else {
                        response = getApplicationContext().getResources().getString(R.string.eventselection);
                    }
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case EVENTSELECTED:
                    response = getApplicationContext().getResources().getString(R.string.eventselecteddelete);
                    response = response.replaceAll("#date#", dateText.getText().toString());
                    response = response.replaceAll("#name#", titleText.getText().toString());
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case SUCCESS:
                    // TODO
                    boolean succ = deleteSelectedEvent();
                    if (succ) {
                        response = getApplicationContext().getResources().getString(R.string.eventdeleted);
                        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                        t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                        break;
                    }
                    break;
                case REJECTED:
                    //TODO
                    response = getApplicationContext().getResources().getString(R.string.wanttocanceldelete);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case CANCELLED:
                    //TODO
                    response = getApplicationContext().getResources().getString(R.string.eventcancelleddelete);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
            }
            systemResponseText.setText(response);
        }

        private void afterError() {
            notUnderstood();
        }

        //Takes user response and analyzes it according to the result sets the state
        private void afterResults() {
            resultText = speechAnalyst.toLowercaseTurkish(resultText);
            Calendar dateCal;
            Calendar timeCal;
            switch (currentState) {
                case TITLE:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = DeleteEventActivity.State.WAITING;
                        break;
                    }
                    if (findEventsOfTitle(resultText) == 1) {
                        count = 0;
                        eventSelectionMade(0);
                        currentState = DeleteEventActivity.State.EVENTSELECTED;
                    } else if (findEventsOfTitle(resultText) > 1) {
                        count = 0;
                        currentState = DeleteEventActivity.State.EVENTSELECTION;
                    } else {
                        count++;
                        currentState = DeleteEventActivity.State.TITLENOTFOUND;
                    }
                    startDialogue();
                    break;
                case EVENTSELECTION:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = DeleteEventActivity.State.WAITING;
                        break;
                    }

                    if (resultText != null) {
                        decideSelectedEvent(resultText);
                        currentState = DeleteEventActivity.State.EVENTSELECTED;
                        startDialogue();
                        break;
                    }
                    break;
                case EVENTSELECTED:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = DeleteEventActivity.State.WAITING;
                        break;
                    }
                    if (speechAnalyst.confirmationGiven(resultText)) {
                        currentState = State.SUCCESS;
                        startDialogue();
                    }else if(speechAnalyst.rejectionGiven(resultText)){
                        currentState = State.REJECTED;
                        startDialogue();
                    }
                    else {
                        notUnderstood();
                    }
                    break;

                case REJECTED:
                    if (speechAnalyst.waitCommandGiven(resultText)) {
                        oldState = currentState;
                        currentState = DeleteEventActivity.State.WAITING;
                        break;
                    }
                    if (speechAnalyst.confirmationGiven(resultText)) {
                        currentState = DeleteEventActivity.State.CANCELLED;
                        startDialogue();
                    } else if (speechAnalyst.rejectionGiven(resultText)) {
                        currentState = State.EVENTSELECTED;
                        startDialogue();
                    }
                    break;


            }
        }

        //If the user response is not understood gives a proper response
        private void notUnderstood() {
            count++;
            if (count == 3) {
                count = 0;
                return;
            }
            String response;
            response = "";
            HashMap<String, String> map = new HashMap<String, String>();
            switch (currentState) {
                case TITLE:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.saytitleofevent);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case TITLENOTFOUND:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.titlenotfound);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case EVENTSELECTED:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.eventselecteddelete);
                    response = response.replaceAll("#date#", dateText.getText().toString());
                    response = response.replaceAll("#name#", titleText.getText().toString());
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case EVENTSELECTION:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.eventselection);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
                    break;
                case REJECTED:
                    response = getApplicationContext().getResources().getString(R.string.unknown) + " " + getApplicationContext().getResources().getString(R.string.wanttocanceldelete);
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                    t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);
            }
            systemResponseText.setText(response);
        }

        //After TTS is finished starts recognition
        private void afterTTS() {
            if ((currentState != DeleteEventActivity.State.DEAD) && (currentState != DeleteEventActivity.State.SUCCESS) && (currentState != State.CANCELLED)) {
                buttonMic2(currentView);
            }
            else {
                currentState = State.DEAD;
                act1Button.performClick();
            }

        }

        public void setOldState() {
            currentState = oldState;
        }
    }

    private boolean decideSelectedEvent(String resultText) {
        ArrayList<Double> comparisonResults = new ArrayList<Double>();


        for (int i = 0; i < eventsOfTitleList.size(); i++) {
            comparisonResults.add(speechAnalystDelete.similarity(resultText, eventsOfTitleList.get(i)));
        }
        Double obj = Collections.max(comparisonResults);
        int ind = comparisonResults.indexOf(obj);

        eventSelectionMade(ind);

        return true;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_event);


        timeCal = Calendar.getInstance();
        dateCal = Calendar.getInstance();
        currentView = findViewById(android.R.id.content);
        extantEventsText = (TextView) this.findViewById(R.id.deleteExistingEvents);
        extantEventsText.setMovementMethod(new ScrollingMovementMethod());
        listeningSuccess = false;
        resultText = "";
        mIsListening = false;

        dateText = (EditText) findViewById(R.id.deleteDateText);
        timeText = (EditText) findViewById(R.id.deleteTimeText);

        userResponseText = (TextView) findViewById(R.id.userResponseTextLabel2);
        systemResponseText = (TextView) findViewById(R.id.systemResponseTextLabel2);

        speechAnalystDelete = new SpeechAnalyst(getApplicationContext());
        Calendar cal = Calendar.getInstance();
        eventDay = cal.get(Calendar.DAY_OF_MONTH);
        eventMonth = cal.get(Calendar.MONTH);
        eventYear = cal.get(Calendar.YEAR);
        eventHour = 13;
        eventMinute = 0;
        calendarElement = new CalendarElement();
        showDialogOnClicks();
        Bundle b = getIntent().getExtras();
        int typeValue = 1;
        if (b != null) {
            typeValue = b.getInt("type");
            if (typeValue == 1) {
                calendarElement.setType(CalendarElement.ElementType.EVENT);
            } else if (typeValue == 2) {
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

        }

        act1Button = (Button) this.findViewById(R.id.deleteBackButton);
        act1Button.setOnClickListener(this);

        mic2ButtonActive = (ImageButton) findViewById(R.id.deleteMic2ButtonActive);
        mic2Button = (ImageButton) findViewById(R.id.deleteMic2Button);

        titleText = (EditText) this.findViewById(R.id.deleteTitleText);
        titleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
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

        DeleteEventActivity.SpeechRecognitionListener listener = new DeleteEventActivity.SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locale = new Locale("tr", "TR");
                    t1.setLanguage(locale);
                    DeleteEventActivity.TextToSpeechListener ttslistener = new DeleteEventActivity.TextToSpeechListener();
                    t1.setOnUtteranceProgressListener(ttslistener);

                    deleteStateMachine = new DeleteEventActivity.DeleteStateMachine(calendarElement);
                    deleteStateMachine.setCurrentState();
                    deleteStateMachine.startDialogue();
                }
            }
        });





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
            deleteStateMachine.afterError();
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
            deleteStateMachine.afterResults();

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
                    deleteStateMachine.afterTTS();
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
            Toast.makeText(DeleteEventActivity.this, "Bu uygulama mikrofon kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();

        } else {
            if (!mIsListening) {

                if (currentState == DeleteEventActivity.State.WAITING){
                    deleteStateMachine.setCurrentState();
                    deleteStateMachine.startDialogue();
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
        if (v.getId() == R.id.deleteBackButton) {
            Intent myIntent = new Intent(v.getContext(), ee492.vecaapp.veca.MainActivity.class);
            currentState = DeleteEventActivity.State.DEAD;
            mSpeechRecognizer.cancel();
            mSpeechRecognizer.destroy();
            finish();

            startActivity(myIntent);
        }
    }


    public void showDialogOnClicks() {

    }

    public void eventSelectionMade(int index) {
        selectedEventID = eventsOfTitleIDList.get(index);
        selectedEventTitle = eventsOfTitleList.get(index);
        selectedEventStartTime = eventsOfTitleSTList.get(index);
        selectedEventEndTime = eventsOfTitleETList.get(index);
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis(Long.parseLong(selectedEventStartTime));
        eventDay = begin.get(Calendar.DAY_OF_MONTH);
        eventMonth = begin.get(Calendar.MONTH) + 1;
        eventYear = begin.get(Calendar.YEAR);
        eventHour = begin.get(Calendar.HOUR_OF_DAY);
        eventMinute = begin.get(Calendar.MINUTE);

        timeCal.set(eventYear,eventMonth-1,eventDay,eventHour,eventMinute);
        dateCal.set(eventYear,eventMonth-1,eventDay);

        setTimeText(timeCal);
        setDateText(dateCal);

        titleText.setText(selectedEventTitle);

        eventTitle = selectedEventTitle;
    }

    //TODO
    public boolean deleteSelectedEvent() {

        long eventID = Long.parseLong(selectedEventID);
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = getContentResolver().delete(deleteUri, null, null);

        return true;

    }

    public int findEventsOfTitle(String eventName) {

        Calendar calendar = Calendar.getInstance();
        int numOfEvents = 0;
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.set(Calendar.DAY_OF_MONTH, nowCalendar.get(Calendar.DAY_OF_MONTH)-1);
        long nowTime = nowCalendar.getTimeInMillis();
        ArrayList<String> nameOfEvent = new ArrayList<String>();
        ArrayList<String> eventIDs = new ArrayList<String>();
        ArrayList<String> startDates = new ArrayList<String>();
        ArrayList<String> endDates = new ArrayList<String>();

        nameOfEvent.clear();

        StringBuilder extantEventsString = new StringBuilder();
        extantEventsString.append("");


        Cursor cursor = getApplicationContext().getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();

        String CNames[] = new String[cursor.getCount()];
        extantEventsString.append("Bulunan etkinlikler:" + System.getProperty("line.separator") + System.getProperty("line.separator"));
        int ind = 0;
        for (int i = 0; i < CNames.length; i++) {
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            if (name != null){
                if (name.toLowerCase().contains(eventName.toLowerCase()) || description.toLowerCase().contains(eventName.toLowerCase())) {
                    if (Long.parseLong(cursor.getString(3)) > nowTime) {
                        ind++;
                        eventIDs.add(cursor.getString(0));
                        nameOfEvent.add(cursor.getString(1));
                        startDates.add(cursor.getString(3));
                        endDates.add(cursor.getString(4));
                        Date begin = new Date(Long.parseLong(cursor.getString(3)));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String dateStr = sdf.format(begin);
                        extantEventsString.append(Integer.toString(ind) + ". " + cursor.getString(1) + System.getProperty("line.separator") + "Tarih: " + dateStr + System.getProperty("line.separator") + System.getProperty("line.separator"));

                    }

                }
            }

            cursor.moveToNext();
        }

        if (eventIDs.size() != 0) {
            extantEventsText.setText(extantEventsString.toString());
        }
        eventsOfTitleList = nameOfEvent;
        eventsOfTitleIDList = eventIDs;
        eventsOfTitleSTList = startDates;
        eventsOfTitleETList = endDates;
        return eventsOfTitleList.size();

    }

    public int findEventsOfDate() {
        extantEventsList = new ArrayList<ArrayList<String>>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(eventYear, eventMonth - 1, eventDay, 0, 0, 0);
        long startDay = calendar.getTimeInMillis();
        calendar.set(eventYear, eventMonth - 1, eventDay, 23, 59, 59);
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
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "_id"}, selection,
                        selectionArgs, null);


        try {
            numOfEvents = cur.getCount();
            if (cur.getCount() > 0) {
                int ind = 0;
                extantEventsString.append(eventDay + "/" + eventMonth + "/" + eventYear + " tarihindeki etkinlikler:" + "\n\n");
                while (cur.moveToNext()) {
                    ind++;
                    ArrayList<String> extantEventInfo = new ArrayList<String>();
                    extantEventInfo.add(cur.getString(0));
                    extantEventInfo.add(cur.getString(6));
                    extantEventsList.add(extantEventInfo);

                    String title = cur.getString(1);
                    Date begin = new Date(getDate(Long.parseLong(cur.getString(3))));
                    Date end = new Date(getDate(Long.parseLong(cur.getString(4))));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String beginStr = sdf.format(begin);
                    String endStr = sdf.format(end);
                    extantEventsString.append(Integer.toString(ind) + ". " + title + System.getProperty("line.separator") + "Başlangıç Saati:" + beginStr + System.getProperty("line.separator") + "Bitiş Saati:" + endStr + "\n\n");
                }
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }

        extantEventsText.setText(extantEventsString.toString());

        return numOfEvents;
    }

    public int findEventsOfMoment() {
        extantEventsList = new ArrayList<ArrayList<String>>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(eventYear, eventMonth - 1, eventDay, eventHour, eventMinute, 0);
        long momentStart = calendar.getTimeInMillis();
        calendar.set(eventYear, eventMonth - 1, eventDay, eventHour + 1, eventMinute, 0);
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
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "_id"}, selection,
                        selectionArgs, null);


        try {
            numOfEvents = cur.getCount();
            if (cur.getCount() > 0) {
                int ind = 0;
                extantEventsString.append("Belirtilen zamandaki etkinlikler:" + "\n");
                while (cur.moveToNext()) {
                    if(!cur.getString(6).equals(selectedEventID)){
                        ind++;
                        ArrayList<String> extantEventInfo = new ArrayList<String>();
                        extantEventInfo.add(cur.getString(0));
                        extantEventInfo.add(cur.getString(6));

                        extantEventsList.add(extantEventInfo);

                        String title = cur.getString(1);
                        Date begin = new Date(getDate(Long.parseLong(cur.getString(3))));
                        Date end = new Date(getDate(Long.parseLong(cur.getString(4))));
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String beginStr = sdf.format(begin);
                        String endStr = sdf.format(end);
                        extantEventsString.append(Integer.toString(ind) + ". " + title + System.getProperty("line.separator") + "Başlangıç Saati:" + beginStr + System.getProperty("line.separator") + "Bitiş Saati:" + endStr + "\n");

                    }
                }
                numOfEvents = extantEventsList.size();
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }

        if(numOfEvents > 0){
            extantEventsText.setText(extantEventsString.toString());
        }


        return numOfEvents;
    }

    public void setDateText(Calendar dateCal) {
        eventDay = dateCal.get(Calendar.DAY_OF_MONTH);
        eventMonth = dateCal.get(Calendar.MONTH) + 1;
        eventYear = dateCal.get(Calendar.YEAR);
        dateText.setText(eventDay + "/" + eventMonth + "/" + eventYear);
        findEventsOfDate();
    }

    public void setTimeText(Calendar timeCal) {
        eventHour = timeCal.get(Calendar.HOUR_OF_DAY);
        eventMinute = timeCal.get(Calendar.MINUTE);
        timeText.setText(String.format("%02d", eventHour) + ":" + String.format("%02d", eventMinute));
        if (calendarElement.isDateSetCheck()) {
            findEventsOfMoment();
        }
    }

    public void setTitleText(String title) {
        eventTitle = title;
        titleText.setText(eventTitle);
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SetEventActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}



