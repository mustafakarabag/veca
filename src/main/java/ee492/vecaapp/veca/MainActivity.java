package ee492.vecaapp.veca;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import java.lang.Math;
import org.jtransforms.fft.DoubleFFT_1D;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Main activity directs user to one of the activities according to the command
    //Artificial buttons to be used only in program, not in the layout
    Button falseCommandButton;
    Button setEventScreenButton;
    Button setReminderScreenButton;
    Button deleteReminderScreenButton;
    Button modifyReminderScreenButton;
    Button showReminderScreenButton;
    Button modifyEventScreenButton;
    Button deleteEventScreenButton;
    private Spinner operationSpinner;
    //Speech recognizer object
    private AudioRecord mAudioRecorder;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    //Text to speech object
    public static TextToSpeech t1;
    //User permissions to be checked
    private int recAudioPermission;
    private int readCalendarPermission;
    private int writeCalendarPermission;
    private int readContactsPermission;
    private int callPermission;
    //SpeechAnalyst object to analyze the user commands
    private SpeechAnalyst speechAnalyst;
    //Calendars to keep time and date information
    private Calendar timeCal = null;
    private Calendar dateCal = null;
    private String eventTitle;
    //Text fields to be updated
    private TextView responseText = null;
    private TextView resultText = null;
    private TextView speakingText = null;
    //Mic Buttons
    private ImageButton micButtonActive = null;
    private ImageButton micButton = null;
    //Contacts list for dialer
    private ArrayList<ArrayList> contacts;
    boolean initializationDone;
    boolean zerothSelection;
    private Recognizer recognizer;
    //Audio Recorder Settings
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isRecording = false;
    private boolean helloCheck = false;
    private Thread recordingThread = null;
    int bufferSize;
    int thrOutput;
    int thrListTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializationDone = false;
        setContentView(R.layout.activity_main);
        //This part matches layout objects with the class objects
        falseCommandButton = (Button) this.findViewById(R.id.falseCommand);
        falseCommandButton.setOnClickListener(this);
        setEventScreenButton = (Button) this.findViewById(R.id.setEventScreen);
        setEventScreenButton.setOnClickListener(this);
        setReminderScreenButton = (Button) this.findViewById(R.id.setReminderScreen);
        setReminderScreenButton.setOnClickListener(this);
        modifyReminderScreenButton = (Button) this.findViewById(R.id.modifyReminderScreen);
        modifyReminderScreenButton.setOnClickListener(this);
        deleteReminderScreenButton = (Button) this.findViewById(R.id.setReminderScreen);
        deleteReminderScreenButton.setOnClickListener(this);
        showReminderScreenButton = (Button) this.findViewById(R.id.showReminderScreen);
        showReminderScreenButton.setOnClickListener(this);
        modifyEventScreenButton = (Button) this.findViewById(R.id.modifyEventScreen);
        modifyEventScreenButton.setOnClickListener(this);
        deleteEventScreenButton = (Button) this.findViewById(R.id.deleteEventScreen);
        deleteEventScreenButton.setOnClickListener(this);
        responseText = (TextView) findViewById(R.id.tResponse);
        resultText = (TextView) findViewById(R.id.tResult);
        speakingText = (TextView) findViewById(R.id.tSpeaking);
        micButtonActive = (ImageButton) findViewById(R.id.micButtonActive);
        micButton = (ImageButton) findViewById(R.id.micButton);

        operationSpinner = (Spinner)findViewById(R.id.operationSpinner);

        eventTitle = "";
        contacts = new ArrayList<ArrayList>();


        //Creates shared preferences and initialize values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        thrOutput = pref.getInt("thrOutput", 20);
        thrListTime = pref.getInt("thrListTime",10);
        System.out.println("list time"+ thrListTime + " " + (thrListTime+10)*100);
        //This part checks permissions at the beginning if one of them is missing user is directed to the settings screen
        recAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        writeCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        readCalendarPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR);
        readContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        //Language settings for the speech recognizer object
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "tr");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,new Long((thrListTime+10)*100 ));
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, new Long((thrListTime+10)*100 ));
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, new Long((thrListTime+10)*100 ));
        //STT listener to get results
        SpeechRecognitionListener listener = new SpeechRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(listener);

        try {
            recognizer = new Recognizer(getApplicationContext());
            System.out.println("recognizer initialization");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Checks permissions, if all of them are accessed, greets
        if (recAudioPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Bu uygulama mikrofon kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (writeCalendarPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Bu uygulama takvim kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (readCalendarPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Bu uygulama takvim kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (readContactsPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Bu uygulama rehber kullanımı için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (callPermission == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(MainActivity.this, "Bu uygulama arama yapmak için izin gerektirmektedir. Lütfen, gerekli izni sağlayın", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            //System.out.println("tts creation");
            //This part creates a tts object and welcomes user
            speechAnalyst = new SpeechAnalyst(getApplicationContext());
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        Locale locale = new Locale("tr", "TR");
                        t1.setLanguage(locale);
                        MainActivity.TextToSpeechListener ttslistener = new MainActivity.TextToSpeechListener();
                        t1.setOnUtteranceProgressListener(ttslistener);
/*                        HashMap<String, String> map = new HashMap<String, String>();
                        String response = getApplicationContext().getResources().getString(R.string.welcome);
                        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ID");
                        //System.out.println("before speak");
                        t1.speak(response, TextToSpeech.QUEUE_FLUSH, map);*/
                    }
                }
            });



            bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
            System.out.println("\n");
            System.out.println(bufferSize);
            BufferElements2Rec = bufferSize;
            startRecording();

        }

        initializationDone = true;

    }

    int BufferElements2Rec = bufferSize;
    int BytesPerElement = 2; // 2 bytes in 16bit format

    private void startRecording() {

        mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        mAudioRecorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
               processSound();
            }
        }, "AudioRecorder Thread");


        recordingThread.start();

    }

    private void processSound() {

        int frameLength = 16;
        int stepLength = frameLength;
        int frameSize = frameLength*RECORDER_SAMPLERATE/1000;
        int stepSize = frameSize;


        double energyThr = frameSize*100;
        double consEnergyThr = 5;


        int thrSil = (int) Math.floor(180.0/(double) frameLength);
        int thrVoi = (int) Math.floor(100.0/ (double) frameLength);
        int voiceCounter = 0;
        int silenceCounter = 0;

        //int frameSize2 = Math.pow(2, ceil(Math.log10()));

        int fLow = (int) Math.floor(300/(double)(RECORDER_SAMPLERATE/2)*(frameSize));
        int fHigh = (int) Math.ceil(3400/(double)(RECORDER_SAMPLERATE/2)*(frameSize));


        boolean inVoice = false;

        double[] thrBuffDouble = new double[Math.round(80/stepLength)];
        Arrays.fill(thrBuffDouble, energyThr);
        LinkedList<Double> thrBuff = new LinkedList<>();
        for(double d : thrBuffDouble) thrBuff.add(d);

        double expectedSNR = 5;
        ArrayList<Double> past320ms = new ArrayList<>( Collections.nCopies(320/frameLength*frameSize, 0.0));

        double nowEnergy = 0;
        double pastEnergy = 0;

        ArrayList<Double> nowUtt = new ArrayList<>();


        short[] sData = new short[BufferElements2Rec];
        ArrayList<Double>  leftData = new ArrayList<Double>();
        double[] nowBuffer = new double[frameSize];
        Double[] nowDoubleData = new Double[frameSize];

        while (isRecording) {
            // gets the voice output from microphone to byte format

            mAudioRecorder.read(sData, 0, BufferElements2Rec);
            Double[] newData = new Double[sData.length];
            for (int i=0; i<newData.length; i++) {
                newData[i] = (((double) sData[i]) )/32768;
            }
            leftData.addAll(Arrays.asList(newData));
            while (leftData.size() >= frameSize){
                try {
                    nowDoubleData = leftData.subList(0,frameSize).toArray(new Double[frameSize]);
                    for (int i=0; i<nowDoubleData.length; i++) {
                        nowBuffer[i] = Double.valueOf(nowDoubleData[i]);
                    }
                   leftData.subList(0,frameSize).clear();

                    //Calculates spectrum and takes only the voice related energies
                    DoubleFFT_1D fftDo = new DoubleFFT_1D(nowBuffer.length);
                    double[] fft = new double[nowBuffer.length * 2];
                    System.arraycopy(nowBuffer, 0, fft, 0, nowBuffer.length);
                    fftDo.realForwardFull(fft);
                    double[] spectrum = new double[fHigh-fLow+1];

                    for (int k=fLow; k<fHigh+1 ; k++)
                    {
                        spectrum[k - fLow] = Math.sqrt(Math.pow(fft[2*k],2) + Math.pow(fft[2*k +1],2));
                    }
                    nowEnergy = 0;
                    for (double bin : spectrum){
                        nowEnergy += bin;
                    }


                    double frameEng = 1.0*nowEnergy-0.1*pastEnergy;




                    if ((energyThr<frameEng) && (consEnergyThr < frameEng)){
                        silenceCounter = 0;
                        if (inVoice){
                            for(double d : nowBuffer) nowUtt.add(d);
                        }else{
                            if (voiceCounter == thrVoi){
                                inVoice = true;
                                setMyText(speakingText,1,0);
                                voiceCounter = 0;
                                nowUtt = new ArrayList<>(past320ms.subList(past320ms.size()-thrVoi*frameSize,past320ms.size()-1));
                                for(double d : nowBuffer) nowUtt.add(d);
                            }else {
                                voiceCounter += 1;
                            }
                        }
                    }else {
                        voiceCounter = 0;
                        if (inVoice){
                            for(double d : nowBuffer) nowUtt.add(d);
                            if (silenceCounter == thrSil){
                                inVoice = false;
                                setMyText(speakingText,0,0);
                                int uttSize = nowUtt.size() - thrSil*frameSize;
                                double[] utt = new double[uttSize];
                                for (int i = 0; i<uttSize; i++){
                                    utt[i] = nowUtt.get(i);
                                }
                                if(recognizer == null){
                                    System.out.println("Recognizer null");
                                }
                                final double result = recognizer.decide(utt);
                                setMyText(speakingText,-1,result);
                                double thrDecision = (thrOutput + 30.0)/100.0;
                                if (result > thrDecision){
                                    stopRecording();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            micButton.performClick();

                                        }
                                    });

                                    return;
                                }
                                //nowUtt.removeR
                                nowUtt = new ArrayList<>();
                            }
                            else {
                                silenceCounter +=1;
                            }
                        }
                        else {
                            thrBuff.removeFirst();
                            thrBuff.add( nowEnergy*expectedSNR/3);
                            Double totEng = new Double(0);
                            for(Double d : thrBuff) totEng += d;
                            energyThr = totEng/thrBuff.size();
                        }
                    }

                    past320ms.subList(0,frameSize).clear();
                    for(double d : nowBuffer) past320ms.add(d);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }


    private void setMyText(final TextView text,final int value,final double score){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (value == 0){
                    text.setText("Not Speaking");
                }else if(value == 1){
                    text.setText("Speaking");
                }else {
                    text.setText("" + score);
                }

            }
        });
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != mAudioRecorder) {
            isRecording = false;
            mAudioRecorder.stop();
            mAudioRecorder.release();
            mAudioRecorder = null;
            recordingThread = null;
            //speakingText.setText("Not Speaking");
            //micButton.performClick();
        }

    }


    protected class TextToSpeechListener extends UtteranceProgressListener {

        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            //At the end of TTS, recognition is triggered
            runOnUiThread(new Runnable() {
                public void run() {
                    micButton.performClick();
                }
            });

        }

        @Override
        public void onError(String utteranceId) {
            Toast.makeText(MainActivity.this, "Lütfen internet ayarlarınızı kontrol edin ve internete bağlı olduğunuzdan emin olun.", Toast.LENGTH_LONG).show();


        }
    }
    //A depreciated method
    public static TextToSpeech getTTS() {
        return t1;
    }

    public void buttonMic(View v) {
        stopRecording();
        micButton.setVisibility(View.INVISIBLE);
        micButtonActive.setVisibility(View.VISIBLE);
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

    }

    public void buttonSpinner(View v) {

            int pos = operationSpinner.getSelectedItemPosition();

            switch (pos){

                case 0:
                    setEventScreenButton.performClick();
                    break;
                case 1:
                    modifyEventScreenButton.performClick();
                    break;
                case 2:
                    deleteEventScreenButton.performClick();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    /*UnitTests testObject = new UnitTests();
                    testObject.performDateTests(speechAnalyst);
                    testObject.performTimeTests(speechAnalyst);
                    testObject.performConfirmationTests(speechAnalyst);
                    testObject.performRejectionTests(speechAnalyst);
                    testObject.performCommandTests(speechAnalyst);
                    testObject.performNumberTests(speechAnalyst);*/
                    break;
            }
    }

    public void buttonHelp(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.helptitle);
        alertDialogBuilder.setMessage(R.string.helpcommands);
                alertDialogBuilder.setPositiveButton("Geri Dön",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void buttonSettings(View v){
        stopRecording();
        Intent setIntent = new Intent(v.getContext(),MinorSettingsActivity.class);
        startActivity(setIntent);
    }

    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View v) {


        //Since transferring objects between activities is complicated, time and date information is transmitted using integers, if they exist
        Bundle b = new Bundle();
        if (timeCal != null) {
            b.putInt("hour", timeCal.get(Calendar.HOUR_OF_DAY));
            b.putInt("min", timeCal.get(Calendar.MINUTE));
        } else {
            b.putInt("hour", -1);
            b.putInt("min", -1);
        }
        if (dateCal != null) {
            b.putInt("day", dateCal.get(Calendar.DAY_OF_MONTH));
            b.putInt("month", dateCal.get(Calendar.MONTH));
            b.putInt("year", dateCal.get(Calendar.YEAR));
        } else {
            b.putInt("day", -1);
            b.putInt("month", -1);
            b.putInt("year", -1);
        }
        if (eventTitle.length() > 2){
            b.putString("title", eventTitle);
        }
        else {
            b.putString("title", "");
        }
        //According to the pressed button user is redirected to a proper activity
        if (v.getId() == R.id.falseCommand) {
            responseText.setText("Yaver: "+ String.valueOf(getResources().getString(R.string.unknown)));
            t1.speak(getResources().getString(R.string.unknown), TextToSpeech.QUEUE_FLUSH, null);
            startRecording();
            return;
        } else if (v.getId() == R.id.setEventScreen) {
            b.putInt("type", 1);
            Intent setIntent = new Intent(v.getContext(), SetEventActivity.class);
            setIntent.putExtras(b);
            startActivity(setIntent);
        } else if (v.getId() == R.id.setReminderScreen) {
            Intent setIntent = new Intent(v.getContext(), SetEventActivity.class);
            b.putInt("type", 2);
            setIntent.putExtras(b);
            startActivity(setIntent);
        } else if (v.getId() == R.id.deleteReminderScreen) {
            Intent setIntent = new Intent(v.getContext(), DeleteEventActivity.class);
            b.putInt("type", 2);
            setIntent.putExtras(b);
            startActivity(setIntent);
        } else if (v.getId() == R.id.modifyReminderScreen) {
            Intent setIntent = new Intent(v.getContext(), ModifyEventActivity.class);
            b.putInt("type", 2);
            setIntent.putExtras(b);
            startActivity(setIntent);
        } else if (v.getId() == R.id.modifyEventScreen) {
            Intent setIntent = new Intent(v.getContext(), ModifyEventActivity.class);
            startActivity(setIntent);
        } else if (v.getId() == R.id.deleteEventScreen){
            Intent setIntent = new Intent(v.getContext(),DeleteEventActivity.class);
            startActivity(setIntent);
        } else if (v.getId() == R.id.showReminderScreen){
            Intent setIntent = new Intent(v.getContext(), ViewReminders.class);
            startActivity(setIntent);
        }



    }

    protected class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            micButtonActive.setVisibility(View.INVISIBLE);
            micButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError(int error) {
            System.out.println("int available " + isOnline());
            if(isOnline()){
                micButtonActive.setVisibility(View.INVISIBLE);
                micButton.setVisibility(View.VISIBLE);
                startRecording();
            }
            else{
                micButtonActive.setVisibility(View.INVISIBLE);
                micButton.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Lütfen internet ayarlarınızı kontrol edin ve internete bağlı olduğunuzdan emin olun.", Toast.LENGTH_LONG).show();

            }
     }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onReadyForSpeech(Bundle params) {
        }

        @Override
        public void onResults(Bundle results) {
            //Gets recognition results
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //String userText = matches.get(0).toLowerCase();
            String userText = matches.get(0);
            userText = userText.replace("yağlar","yaver");
            userText = userText.replace("yapar","yaver");
            resultText.setText("Siz: " + userText);
            System.out.println("user text : " + userText);
            userText = speechAnalyst.toLowercaseTurkish(userText);
            System.out.println(userText);
            //Gets time and date info from the user response
            dateCal= speechAnalyst.getDate(userText);
            userText =userText.replaceAll(speechAnalyst.lastUsedText, "");
            timeCal = speechAnalyst.getTime(userText);
            userText = userText.replaceAll(speechAnalyst.lastUsedText,"");
            //This part tries to understand what user wants
            boolean callCommand = speechAnalyst.callCommandGiven(userText);
            CalendarElement.ElementType typeModifyResult = speechAnalyst.getModifyType(userText);
            CalendarElement.ElementType typeResult = speechAnalyst.getType(userText);
            CalendarElement.ElementType typeShowResult = speechAnalyst.getShowType(userText);
            if (typeResult != CalendarElement.ElementType.UNKNOWN){
                for (int ind =0; ind<speechAnalyst.lastUsedWords.size();ind++){
                    userText = userText.replaceFirst(speechAnalyst.lastUsedWords.get(ind),"");
                }
                for (int ind=0;ind<SpeechAnalyst.discardedWords.size();ind++){
                    userText = userText.replaceAll(SpeechAnalyst.discardedWords.get(ind),"");
                }
                for (int ind=0;ind<SpeechAnalyst.dayIntervals.size();ind++){
                    userText = userText.replaceAll(SpeechAnalyst.dayIntervals.get(ind),"");
                }
                userText = userText.replaceAll("\\b\\w{1,2}\\b\\s?", "");
                userText = userText.replaceAll("\\s+", " ");
                userText = userText.trim();
                eventTitle = userText;
            }
            CalendarElement.ElementType typeDeleteResult = speechAnalyst.getDeleteType(matches.get(0));
            //According to the user command directs user to a proper activity
            if (typeModifyResult == CalendarElement.ElementType.EVENT) {
                modifyEventScreenButton.performClick();
            } else if (typeDeleteResult == CalendarElement.ElementType.EVENT) {
                deleteEventScreenButton.performClick();
            } else if (typeResult == CalendarElement.ElementType.EVENT) {
                setEventScreenButton.performClick();
            } else if (typeShowResult == CalendarElement.ElementType.REMINDER){
                showReminderScreenButton.performClick();
            }
            else if (typeResult == CalendarElement.ElementType.REMINDER) {
                setReminderScreenButton.performClick();
            }
            else if (callCommand) {
                //If user says "ara", "call" explicityly finds the contact with minimum string distance and calls him
                int ind = 0;
                double maxScore = 0;
                getContacts();
                userText = userText.replace("ara", "");
                for (int i = 0; i < contacts.size(); i++) {
                    String name = (String) contacts.get(i).get(1);
                    double score = speechAnalyst.similarity(userText, name);
                    if (maxScore < score) {
                        maxScore = score;
                        ind = i;
                    }
                }

                if (maxScore > 0.6) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + (String) contacts.get(ind).get(2)));

                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                } else {
                    falseCommandButton.performClick();
                }
            } else {
                falseCommandButton.performClick();
            }


        }
        // matches are the return values of speech recognition engine
        // Use these values for whatever you wish to do


        @Override
        public void onRmsChanged(float rmsdB) {
        }


    }


    public void getContacts() {
    //Reads contacts and keeps ids, names and telephone numbers
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    ArrayList<String> curContact = new ArrayList<String>();
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        curContact.add(0, id);
                        curContact.add(1, name);
                        curContact.add(2, phoneNo);
                        contacts.add(curContact);
                    }
                    pCur.close();
                }
            }
        }
    }


}





