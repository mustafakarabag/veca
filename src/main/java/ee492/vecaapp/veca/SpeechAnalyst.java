package ee492.vecaapp.veca;

import android.content.Context;
import android.icu.text.SymbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mustafa on 9.10.2016.
 */

public class SpeechAnalyst {

    Context appContext = null;
    public static ArrayList<String> eventSyn = new ArrayList<String>();
    public static ArrayList<String> reminderSyn = new ArrayList<String>();
    public static ArrayList<String> commandSyn = new ArrayList<String>();
    public static ArrayList<String> reminderCommandSyn = new ArrayList<String>();
    public static ArrayList<String> dayIntervals = new ArrayList<String>();
    public static ArrayList<String> timeSyn = new ArrayList<String>();
    public static ArrayList<String> dateSyn = new ArrayList<String>();
    public static ArrayList<String> changeSyn = new ArrayList<String>();
    public static ArrayList<String> thisSyn = new ArrayList<String>();
    public static ArrayList<String> titleSyn = new ArrayList<String>();
    public static ArrayList<String> waitSyn = new ArrayList<String>();
    public static ArrayList<String> deleteSyn = new ArrayList<String>();
    public static ArrayList<String> discardedWords = new ArrayList<String>();
    public static ArrayList<String> confirmationSyn = new ArrayList<String>();
    public static ArrayList<String> rejectionSyn = new ArrayList<String>();
    public static ArrayList<String> showSyn = new ArrayList<String>();
    public String lastUsedText;
    public ArrayList<String> lastUsedWords;
    public SpeechAnalyst(Context appContext){
    this.appContext= appContext;
        eventSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.eventsyn)))  ;
        reminderSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.remindersyn)))  ;
        commandSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.commandsyn)))  ;
        reminderCommandSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.remindercommandsyn)))  ;
        dayIntervals = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.dayintervals)))  ;
        timeSyn =getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.timesyn)));
        dateSyn =getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.datesyn)));
        changeSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.changesyn)));
        titleSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.titlesyn)));
        waitSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.waitsyn)));
        deleteSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.deletesyn)));
        discardedWords = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.discardedwords)));
        confirmationSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.confirmationsyn)));
        rejectionSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.rejectionsyn)));
        showSyn = getWordsFromSentence(String.valueOf(appContext.getResources().getString(R.string.showsyn)));
    }

    public CalendarElement.ElementType getType(String userText){
        lastUsedWords = new ArrayList<String>();
        boolean commandGiven = false;
        boolean reminderCommandGiven = false;
        boolean eventSynGiven = false;
        boolean reminderSynGiven = false;
        for(String word: commandSyn){
            String pattern = "(\\b" +word +"[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group());
                commandGiven = true;
                break;
            }
        }
        for(String word: reminderCommandSyn){
            String pattern = "(\\b" +word +"[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group());
                reminderCommandGiven = true;
                break;
            }
        }
        for(String word: eventSyn){
            String pattern = "(\\b" +word +"[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group());
                eventSynGiven = true;
                break;
            }
        }

        for(String word: reminderSyn){
            String pattern = "(\\b" +word +"[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group());
                reminderSynGiven = true;
                break;
            }
        }
        addDiscardedWords(userText);
        if (commandGiven){
            if (eventSynGiven){

                return CalendarElement.ElementType.EVENT;
            }
            else if(reminderSynGiven){
                return CalendarElement.ElementType.REMINDER;
            }
        }
        else if(reminderCommandGiven){
            return CalendarElement.ElementType.REMINDER;
        }
        return CalendarElement.ElementType.UNKNOWN;
    }

    public CalendarElement.ElementType getModifyType(String userText){

        boolean commandGiven = false;
        boolean reminderCommandGiven = false;
        boolean eventSynGiven = false;
        boolean reminderSynGiven = false;
        for(String word: changeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: eventSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                eventSynGiven = true;
                break;
            }
        }

        if (commandGiven){
            if (eventSynGiven){

                return CalendarElement.ElementType.EVENT;
            }
        }
        return CalendarElement.ElementType.UNKNOWN;
    }

    public CalendarElement.ElementType getDeleteType(String userText){

        boolean commandGiven = false;
        boolean eventSynGiven = false;
        boolean reminderSynGiven = false;
        for(String word: deleteSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: eventSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                eventSynGiven = true;
                break;
            }
        }

        if (commandGiven){
            if (eventSynGiven){
                return CalendarElement.ElementType.EVENT;
            }
        }
        return CalendarElement.ElementType.UNKNOWN;
    }

    public ArrayList<String> getWordsFromSentence (String sentence){
        ArrayList<String> wordList =  new ArrayList<String>(Arrays.asList(sentence.split("\\W+")));
        return wordList;
    }

    public Calendar getTime (String userText){
        Calendar timeCal = Calendar.getInstance();
        userText = removeCounts(userText);
        lastUsedText = "";
        boolean pmCheck = false;
        String pattern = "öğleden sonra";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);

        if (m.find()){
            pmCheck = true;
        }

        boolean noonCheck = false;
        pattern = "öğle";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            noonCheck = true;
        }


        boolean morningCheck = false;
        pattern = "sabah";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            morningCheck = true;
        }

        boolean dayCheck = false;
        pattern = "gündüz";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            morningCheck = true;
        }


        boolean nightCheck = false;
        pattern = "gece";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            nightCheck = true;
        }

        boolean eveningCheck = false;
        pattern = "akşam";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            eveningCheck = true;
        }


        int hour = -1;
        int min = -1;
        boolean timeFound = false;

        if(!timeFound){
            pattern = "(\\w+|\\d)( )(\\w+|\\d)[iıuü]( )(\\w+|\\d)( )(\\bgeçe[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if (getNumber(m.group(3)) != -1){
                    timeFound =true;
                    hour = getNumber(m.group(1) + " "+ m.group(3));
                    min = getNumber(m.group(5));
                    lastUsedText = m.group(0);
                }
            }
        }
        if(!timeFound){
            pattern = "(\\w+|\\d)[ıiuü]( )(\\w+|\\d)( )(\\w+|\\d)( )(\\bgeçe[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if (getNumber(m.group(1)) != -1){
                    timeFound =true;
                    hour = getNumber(m.group(1));
                    min = getNumber(m.group(3)  + " "+ m.group(5));
                    lastUsedText = m.group(0);
                }
            }

        }

        if(!timeFound){
            pattern = "(\\w+|\\d)( )(\\w+|\\d)( )(\\bgeçe[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                timeFound =true;
                hour = getNumber(m.group(1));
                min = getNumber(m.group(3));
                lastUsedText = m.group(0);
            }
        }

        if(!timeFound){
            pattern = "(\\w+|\\d)( )(\\w+|\\d)[ae]( )(\\w+|\\d)( )(\\bkala[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){

                if (getNumber(m.group(3))!=-1){
                    timeFound =true;
                    hour = getNumber(m.group(1) + " "+ m.group(3)) -1 ;
                    min = 60 - getNumber(m.group(5));
                    lastUsedText = m.group(0);
                }

            }
        }
        if(!timeFound){
            pattern = "(\\w+|\\d)[ae]( )(\\w+|\\d)( )(\\w+|\\d)( )(\\bkala[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if (getNumber(m.group(1)) != -1){
                    timeFound =true;
                    hour = getNumber(m.group(1)) -1 ;
                    min = 60- getNumber(m.group(3)  + " "+ m.group(5));
                    lastUsedText = m.group(0);
                }
            }
        }

        if(!timeFound){
            pattern = "(\\w+|\\d)( )(\\w+|\\d)( )(\\bkala[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                timeFound =true;
                hour = getNumber(m.group(1)) -1 ;
                min = 60 - getNumber(m.group(3));
                lastUsedText = m.group(0);
            }
        }


        if (!timeFound){
            pattern = "(\\d{1,2})([: -.])(\\d{2})";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                timeFound =true;
                hour = getNumber(m.group(1));
                min = getNumber(m.group(3));
                lastUsedText = m.group(0);
            }
        }

        if(!timeFound){
            pattern ="(\\w+|\\d)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            while (m.find()){
                String hourText = m.group(1);
                if (getNumber(hourText) != -1){
                    lastUsedText = m.group(0);
                    pattern ="(\\b" + hourText+ "[\\p{L}|']*)( )(\\w+|\\d)";
                    p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    m = p.matcher(userText);
                    if (m.find()){
                        String minuteText =m.group(3);
                        if (getNumber(minuteText) !=-1){
                            hour = getNumber(hourText);
                            min = getNumber(minuteText);
                            timeFound =true;
                            lastUsedText = m.group(0);
                            break;
                        }

                    }

                    hour = getNumber(hourText);
                    timeFound = true;
                    break;
                }
            }
        }


        //TODO
        if(!timeFound){
            pattern ="(\\w+|\\d)( )(\\w+|\\d)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            while (m.find()){

                String hourText = m.group();
                if (getNumber(hourText) != -1){
                    lastUsedText = m.group(0);
                    pattern =hourText + "( )(\\w+|\\d)";
                    p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    m = p.matcher(userText);
                    if (m.find()){
                        String minuteText =m.group(2);
                        if (getNumber(minuteText) !=-1){
                            hour = getNumber(hourText);
                            min = getNumber(minuteText);
                            timeFound =true;
                            lastUsedText = m.group(0);
                            break;
                        }

                    }

                    hour = getNumber(hourText);
                    timeFound = true;
                    break;
                }
            }
        }




        if(!timeFound){
            for(String word: dayIntervals ){
                pattern = "(\\b" + word+ "[\\p{L}|']*)( )(\\w+|\\d)";
                p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                m = p.matcher(userText);
                if (m.find()){
                    timeFound =true;
                    hour = getNumber(m.group(3));
                    lastUsedText = m.group(0);
                    break;
                }
            }
        }




        if (hour < 13 && hour != -1){
            if(nightCheck){
                if (hour >7){
                    hour +=12;
                    if (hour == 24){
                        hour = 0;
                    }
                }
            }
            else if(eveningCheck){
                if (hour >2){
                    hour +=12;
                    if (hour == 24){
                        hour = 0;
                    }
                }
            }
            else if(dayCheck){
                if (hour < 6){
                    hour +=12;
                    if (hour == 24){
                        hour = 0;
                    }
                }
            }
            else if(morningCheck){

            }
            else if(pmCheck){
                hour +=12;
                if (hour == 24){
                    hour = 0;
                }
            }
            else if(noonCheck){
                if (hour < 7){
                    hour +=12;
                    if (hour == 24){
                        hour = 0;
                    }
                }
            }
            else if (hour < 8){
                hour +=12;
                if (hour == 24){
                    hour = 0;
                }
            }
        }


        if(hour != -1){
            timeCal.set(Calendar.HOUR_OF_DAY, hour);
            timeCal.set(Calendar.SECOND, 0);
            if (min != -1){
                timeCal.set(Calendar.MINUTE,min);
            }
            else {
                timeCal.set(Calendar.MINUTE,0);
            }
            return timeCal;
        }


        return  null;
    }

    public Calendar getDate(String userText){
        Calendar dateCal = Calendar.getInstance();
        userText = toLowercaseTurkish(userText);
        lastUsedText = "";
        boolean todayCheck = false;
        String pattern = appContext.getResources().getString(R.string.today);
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);
        if (m.find()){
            todayCheck = true;
            return dateCal;
        }

        boolean tomorrowCheck = false;
        pattern = appContext.getResources().getString(R.string.tomorrow);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            tomorrowCheck = true;
            dateCal.add(Calendar.DATE, 1);
            return dateCal;
        }

        boolean nextCheck = false;
        StringTokenizer stNext = new StringTokenizer(appContext.getResources().getString(R.string.next));
        while (stNext.hasMoreTokens()) {
            String dummy = stNext.nextToken();
            pattern = dummy;
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                nextCheck = true;
                break;
            }
        }


        boolean nextWeekCheck = false;
        pattern = appContext.getResources().getString(R.string.nextweek1);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            lastUsedText = m.group(0);
            nextWeekCheck = true;
            int dayOfWeek = dateCal.get(Calendar.DAY_OF_WEEK);
            pattern = "(gelecek)( )(\\bhafta[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if(getDayNumber(m.group(5)) != -1){
                    dayOfWeek =getDayNumber(m.group(5));
                    lastUsedText = m.group(0);
                }
            }

            if (dayOfWeek<dateCal.get(Calendar.DAY_OF_WEEK)){
                dayOfWeek +=7;
            }

            dateCal.add(Calendar.DATE, 7 -  dateCal.get(Calendar.DAY_OF_WEEK) + dayOfWeek);
            return dateCal;
        }

        pattern = appContext.getResources().getString(R.string.nextweek2);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);

        if (m.find()){
            lastUsedText = m.group(0);
            nextWeekCheck = true;
            int dayOfWeek = dateCal.get(Calendar.DAY_OF_MONTH);
            pattern = "(önümüzdeki)( )(\\bhafta[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if(getDayNumber(m.group(5)) != -1){
                    dayOfWeek =getDayNumber(m.group(5));
                    lastUsedText = m.group(0);
                }
            }

            if (dayOfWeek<=dateCal.get(Calendar.DAY_OF_WEEK)){
                dayOfWeek +=7;
            }

            dateCal.add(Calendar.DATE, 7 -  dateCal.get(Calendar.DAY_OF_WEEK) + dayOfWeek);
            return dateCal;
        }


        boolean nextMonthCheck = false;
        pattern = appContext.getResources().getString(R.string.nextmonth1);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            lastUsedText = m.group(0);
            nextMonthCheck = true;
            int dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
            pattern = "(gelecek)( )(\\bay[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);

            if (m.find()){
                dayOfMonth = getNumber(m.group(5));
            }

            dateCal.add(Calendar.MONTH, 1);
            if(dayOfMonth != -1){
                dateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastUsedText = m.group(0);
            }
            return dateCal;

        }

        dateCal = Calendar.getInstance();
        pattern = appContext.getResources().getString(R.string.nextmonth2);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            lastUsedText = m.group(0);
            nextMonthCheck = true;
            int dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
            pattern = "(önümüzdeki)( )(\\bay[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);

            if (m.find()){
                dayOfMonth = getNumber(m.group(5));
            }

            dateCal.add(Calendar.MONTH, 1);
            if(dayOfMonth != -1){
                dateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastUsedText = m.group(0);
            }

            return dateCal;

        }


        dateCal = Calendar.getInstance();
        boolean thisWeekCheck = false;
        pattern = appContext.getResources().getString(R.string.thisweek);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            lastUsedText = m.group(0);
            thisWeekCheck = true;
            int dayOfWeek = dateCal.get(Calendar.DAY_OF_WEEK);
            pattern = "(bu)( )(\\bhafta[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                if(getDayNumber(m.group(5)) != -1){
                    dayOfWeek =getDayNumber(m.group(5));
                    lastUsedText = m.group(0);
                }

            }
            if (dayOfWeek<=dateCal.get(Calendar.DAY_OF_WEEK)){
                dayOfWeek +=7;
            }
            dateCal.add(Calendar.DATE, -  dateCal.get(Calendar.DAY_OF_WEEK) + dayOfWeek);
            return dateCal;

        }


        dateCal = Calendar.getInstance();
        boolean thisMonthCheck = false;
        pattern = appContext.getResources().getString(R.string.thismonth);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            lastUsedText = m.group(0);
            thisMonthCheck = true;
            int dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
            pattern = "(bu)( )(\\bay[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                dayOfMonth = getNumber(m.group(5));
            }
            if(dayOfMonth != -1){
                dateCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                lastUsedText = m.group(0);
            }
            return dateCal;

        }

        dateCal = Calendar.getInstance();
        // 12 aralık 2017
        String monthName = "";
        int dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
        int year = dateCal.get(Calendar.YEAR);
        for(int i=0;i<12;i++){
            monthName = appContext.getResources().getString(appContext.getResources().getIdentifier("mo" + Integer.toString(i), "string", appContext.getPackageName()));
            pattern = "(\\w+)( )(\\b"+ monthName + "[\\p{L}|']*)( )(\\w+)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                lastUsedText = m.group(3);
                if(getNumber(m.group(5)) != -1){
                    year =getNumber(m.group(5));
                    lastUsedText = lastUsedText+ m.group(4) + m.group(5);
                }
                if(getNumber(m.group(1)) != -1){
                    dayOfMonth =getNumber(m.group(1));
                    lastUsedText = m.group(1)+m.group(2)+lastUsedText;
                }
                Calendar dummyCalendar = Calendar.getInstance();
                dummyCalendar.set(year,i,dayOfMonth);
                if (dummyCalendar.compareTo(dateCal)<0 && getNumber(m.group(5)) == -1){
                    dateCal.set(year+1,i,dayOfMonth);
                }
                else {
                    dateCal.set(year,i,dayOfMonth);
                }
                if (year<1900){
                    System.out.println("break happened");
                    break;
                }
                return dateCal;
            }
        }

        dateCal = Calendar.getInstance();
        dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
        year = dateCal.get(Calendar.YEAR);
        for(int i=0;i<12;i++){
            monthName = appContext.getResources().getString(appContext.getResources().getIdentifier("mo" + Integer.toString(i), "string", appContext.getPackageName()));
            pattern = "(\\w+)( )(\\b"+ monthName + "[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                lastUsedText = m.group(3);
                if(getNumber(m.group(1)) != -1){
                    dayOfMonth =getNumber(m.group(1));
                    lastUsedText = m.group(0);
                }
                Calendar dummyCalendar = Calendar.getInstance();
                dummyCalendar.set(year,i,dayOfMonth);
                if (dummyCalendar.compareTo(dateCal)<0){
                    dateCal.set(year+1,i,dayOfMonth);
                }
                else {
                    dateCal.set(year,i,dayOfMonth);
                }
                System.out.println("12 aralık" + year);
                return dateCal;
            }
        }

        dateCal = Calendar.getInstance();
        dayOfMonth = dateCal.get(Calendar.DAY_OF_MONTH);
        year = dateCal.get(Calendar.YEAR);
        for(int i=0;i<12;i++){
            monthName = appContext.getResources().getString(appContext.getResources().getIdentifier("mo" + Integer.toString(i), "string", appContext.getPackageName()));
            pattern = "(\\b"+ monthName + "[\\p{L}|']*)";
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(userText);
            if (m.find()){
                lastUsedText = m.group(0);
                Calendar dummyCalendar = Calendar.getInstance();
                dummyCalendar.set(year,i,dayOfMonth);
                if (dummyCalendar.compareTo(dateCal)<0){
                    dateCal.set(year+1,i,dayOfMonth);
                }
                else {
                    dateCal.set(year,i,dayOfMonth);
                }

                return dateCal;
            }
        }



/*        int dayOfWeek;
        pattern = "(\\w+)";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        int ind = 1;
        if (m.find()){
            System.out.println(ind);
            System.out.println(m.group(ind));
            if(getDayNumber(m.group(ind)) != -1){
                dayOfWeek =getDayNumber(m.group(ind));
                if (dayOfWeek<dateCal.get(Calendar.DAY_OF_WEEK)){
                    dayOfWeek +=7;
                }
                dateCal.add(Calendar.DATE, -  dateCal.get(Calendar.DAY_OF_WEEK) + dayOfWeek);
                return dateCal;
            }
        }*/
        dateCal = Calendar.getInstance();
        int dayOfWeek;
        StringTokenizer st = new StringTokenizer(userText);
        while (st.hasMoreTokens()) {
            String dummy = st.nextToken();
            if(getDayNumber(dummy) != -1){
                lastUsedText = dummy;
                dayOfWeek =getDayNumber(dummy);
                if (dayOfWeek<dateCal.get(Calendar.DAY_OF_WEEK)){
                    dayOfWeek +=7;
                }
                if (nextCheck){
                    dayOfWeek +=7;
                }
                dateCal.add(Calendar.DATE, -  dateCal.get(Calendar.DAY_OF_WEEK) + dayOfWeek);
                return dateCal;
            }
        }

        dateCal = Calendar.getInstance();
        pattern = "(\\w+)( )" + appContext.getResources().getString(R.string.daysafter);
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(userText);
        if (m.find()){
            if(getNumber(m.group(1)) != -1){
                lastUsedText = m.group(0);
                System.out.println("Last used" + lastUsedText);
                dateCal.add(Calendar.DATE, getNumber(m.group(1)));
                return dateCal;
            }

        }


        return null;
    }

    public boolean confirmationGiven(String userText){

        boolean confirmationCheck = false;

        for(String word: confirmationSyn){
            String pattern = "(\\b"+ word + "[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                confirmationCheck = true;
                break;
            }
        }


/*        String pattern = "("+ appContext.getResources().getString(R.string.yes) +")";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);
        if (m.find()){
            confirmationCheck = true;

        }*/
        return confirmationCheck;
    }

    public boolean rejectionGiven(String userText) {
        boolean rejectionCheck = false;

        for(String word: rejectionSyn){
            String pattern = "(\\b"+ word + "[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                rejectionCheck = true;
                break;
            }
        }


/*        String pattern = "("+ appContext.getResources().getString(R.string.no) +")";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);
        if (m.find()){
            confirmationCheck = true;

        }*/
        return rejectionCheck;
    }

    public boolean fixTime(String userText){
        boolean commandGiven = false;
        boolean timeSynGiven = false;
        for(String word: changeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: timeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                timeSynGiven = true;
                break;
            }
        }
        return commandGiven && timeSynGiven;
    }

    public boolean fixDate(String userText){
        boolean commandGiven = false;
        boolean dateSynGiven = false;
        for(String word: changeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: dateSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                dateSynGiven = true;
                break;
            }
        }
        return commandGiven && dateSynGiven;
    }

    public boolean fixTitle(String userText){
        boolean commandGiven = false;
        boolean titleSynGiven = false;
        for(String word: changeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: titleSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                titleSynGiven = true;
                break;
            }
        }
        return commandGiven && titleSynGiven;
    }

    public boolean fixType(String userText){
        boolean commandGiven = false;
        boolean typeSynGiven = false;
        for(String word: changeSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                commandGiven = true;
                break;
            }
        }
        for(String word: eventSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                typeSynGiven = true;
                break;
            }
        }
        for(String word: reminderSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                typeSynGiven = true;
                break;
            }
        }
        return commandGiven && typeSynGiven;
    }

    public int getDayNumber(String dayText){
        String dayPattern ="";
        for(int i=7; i>0; i--){
            dayPattern = appContext.getResources().getString(appContext.getResources().getIdentifier("day" + Integer.toString(i), "string", appContext.getPackageName()));

            Pattern p = Pattern.compile(dayPattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(dayText);
            if(m.find()){
                return i;
            }
        }
        return -1;
    }

    public int getNumber(String numberText){
        int number = 0;

        String pattern = "(\\d+)";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(numberText);

        if(m.find()){
            return Integer.parseInt(m.group(0));
        }
        pattern = "("+ appContext.getResources().getString(appContext.getResources().getIdentifier("n15c", "string",appContext.getPackageName()))+ ")";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(numberText);

        if(m.find()){
            return 15;
        }

        pattern = "("+ appContext.getResources().getString(appContext.getResources().getIdentifier("n30b", "string",appContext.getPackageName()))+ ")";
        p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = p.matcher(numberText);

        if(m.find()){
            return 30;
        }

        String digitText = "";
        for (int i=1; i<10; i++){
            digitText = "n" + Integer.toString(i);

            digitText = appContext.getResources().getString(appContext.getResources().getIdentifier(digitText, "string", appContext.getPackageName()));

            pattern = digitText;
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(numberText);

            if(m.find()){
                number +=i;
                break;
            }
        }

        for (int i=10; i<100; i+=10){
            digitText = "n" + Integer.toString(i);

            digitText = appContext.getResources().getString(appContext.getResources().getIdentifier(digitText, "string", appContext.getPackageName()));

            pattern = digitText;
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            m = p.matcher(numberText);

            if(m.find()){
                number +=i;
                return number;
            }
        }

        if(number != 0){
            return number;
        }


        return -1;
    }

    public boolean waitCommandGiven(String userText){
        boolean waitCheck = false;
        for(String word: waitSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                waitCheck = true;
                break;
            }
        }
        return waitCheck;
    }
    public boolean showCommandGiven(String userText){
        boolean showCheck = false;
        for(String word: showSyn){
            String pattern = "("+word+")";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                showCheck = true;
                break;
            }
        }
        return showCheck;
    }

    public double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    private int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }


    public boolean callCommandGiven(String userText) {

        boolean callCheck = false;
        String pattern = "("+ appContext.getResources().getString(R.string.callcommand) +")";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);
        if (m.find()){
            callCheck = true;
        }
        return callCheck;
    }

    public boolean testCommandGiven(String userText) {

        boolean testCheck = false;
        String pattern = "("+ appContext.getResources().getString(R.string.testcase) +")";
        Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = p.matcher(userText);
        if (m.find()){
            testCheck = true;
        }
        return testCheck;
    }


    public void addDiscardedWords(String userText){

        for(String word: discardedWords){
            String pattern = "(\\b"+ word + "[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group(0));
            }
        }
    }

    public String toLowercaseTurkish(String userText){
        System.out.println(userText);
        String trText = userText;
        trText = trText.replace('Ş','ş');
        trText = trText.replace('I','ı');
        trText = trText.replace('İ','i');
        trText = trText.replace('Ü','ü');
        trText = trText.replace('Ç','ç');
        trText = trText.replace('Ğ','ğ');
        trText = trText.replace('Ö','ö');
        System.out.println(trText);
        return trText;
    }

    public String removeCounts(String userText){
        for(String word: eventSyn){
            String pattern = "(bir)( )(\\b"+ word + "[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                userText = userText.replaceAll("bir", "");
            }
        }
        for(String word: reminderSyn){
            String pattern = "(bir)( )(\\b"+ word + "[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                userText = userText.replaceAll("bir", "");
            }
        }
        return userText;
    }

    public CalendarElement.ElementType getShowType(String userText) {

        boolean showCommand = showCommandGiven(userText);
        boolean reminderSynGiven = false;


        for(String word: reminderSyn){
            String pattern = "(\\b" +word +"[\\p{L}|']*)";
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(userText);
            if (m.find()){
                lastUsedWords.add(m.group());
                reminderSynGiven = true;
                break;
            }
        }

        if (showCommand){
            if (reminderSynGiven){
                return CalendarElement.ElementType.REMINDER;
            }


        }
        return CalendarElement.ElementType.UNKNOWN;
    }
}
