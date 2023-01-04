package ee492.vecaapp.veca;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mustafa on 14.10.2016.
 */

public class CalendarElement {
    public enum ElementType {
        EVENT,
        REMINDER,
        UNKNOWN
    }
    private boolean typeSetCheck = false;
    private boolean titleSetCheck = false;
    private boolean dateSetCheck = false;
    private boolean timeSetCheck = false;
    private boolean locationSetCheck = false;

    private ElementType typeInfo;
    private String titleInfo;
    private String locationInfo;
    private Calendar timeInfo;
    private Calendar dateInfo;

    public CalendarElement(){
        timeInfo = null;
        dateInfo = null;
        titleInfo = "";
        locationInfo = "";
        typeInfo = ElementType.UNKNOWN;
    }

    public boolean setType(ElementType type){

        if (type!=ElementType.UNKNOWN){
            this.typeInfo = type;
            typeSetCheck = true;
            return true;
        }
        return false;
    }

    public boolean setDate(Calendar dateCal){
        if (dateCal!=null){
            dateSetCheck = true;
            dateInfo = dateCal;
            return true;
        }
        return false;
    }

    public boolean setTime(Calendar timeCal){
        if (timeCal != null){
            timeInfo = timeCal;
            timeSetCheck = true;
            return true;
        }
        return false;
    }

    public boolean setTitle(String title){
        if (title != null){
            if (title != ""){
                titleInfo = title;
                titleSetCheck = true;
                return true;
            }
        }
        return false;
    }

    /*
    public void setTimeInfo(Calendar time){
        timeInfo = time;
        timeSetCheck = true;
    }


    public void setDateInfo(Calendar date){
        timeInfo = date;
        dateSetCheck = true;
    }

    */

    public void clearTimeInfo(){
        timeInfo = null;
        timeSetCheck = false;
    }

    public void clearDateInfo(){
        dateInfo = null;
        dateSetCheck = false;
    }

    public void clearTitleInfo(){
        titleInfo = "";
        titleSetCheck = false;
    }

    public ElementType getType(){
        return this.typeInfo;
    }

    public Calendar getTimeInfo(){
        return timeInfo;
    }

    public Calendar getDateInfo(){
        return timeInfo;
    }


    public boolean isTitleSetCheck(){
        return titleSetCheck; }
    public boolean isDateSetCheck(){
        return dateSetCheck;
    }
    public boolean isTypeSetCheck(){
        return typeSetCheck;
    }
    public boolean isTimeSetCheck(){
        return timeSetCheck;
    }

}
