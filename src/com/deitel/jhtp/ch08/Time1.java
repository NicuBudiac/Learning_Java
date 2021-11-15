package com.deitel.jhtp.ch08;

public class Time1 {
    private int hour;
    private int minute;
    private int second;

    // set a new time value using universal time: throw an
    // exception if the hour, minute and second is invalid
    public void setTime(int h , int m , int s)
    {
        if ((h >= 0 && h < 24) && (m >= 0 && m < 60) && (s >= 0 && s < 60) )
        {
            hour = h;
            minute = m;
            second = s;
        } // end if
        else
            throw new IllegalArgumentException("hour, minute and/or second was out of range" );
    } // end the method set time


    // convert to String in universal-time format (HH:MM:SS)
    public String toUniversalString()
    {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    } // end method toUniversalString

    // convert to String in standard-time format (H:MM:SS AM or PM)
    public String toString()
    {
        return String.format("%d:%02d:%02d %s",
                ((hour == 0 || hour == 12) ? 12: hour % 12), minute, second, (hour < 12 ? "AM" : "PM") );
    }




}
