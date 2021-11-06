public class Time2
{
    private int hour;
    private int minute;
    private int second;

    // Time2 no argument constructor
    // initialize each instance variable to zero

    public Time2()
    {
        this( 0, 0, 0 ); // invoke Time2 constructor with three arguments
    } // end Time2 no-argument constructor


    // Time2 constructor: hour supplied, minute and second defaulted to 0
    public Time2(int h )
    {
        this( h, 0, 0 ); // invoke Time2 constructor with three arguments
    }// end Time2 one-argument constructor



    // Time2 constructor: hour and minute supplied, second defaulted to 0
    public Time2(int h , int m)
    {
        this( h, m, 0 ); // invoke Time2 constructor with three arguments
    } // end Time2 two-argument constructor



    // Time2 constructor: hour, minute and second supplied
    public Time2(int h, int m, int s)
    {
        setTime(h,m,s); // invoke Time2 constructor with three arguments
    } // end Time2 three-argument constructor

    public Time2(Time2 time)
    {
        this(time.getHour(), time.getMinute(), time.getSecond() );
    }

    // setTime
    public void setTime(int h, int m, int s)
    {
        setHour( h );
        setSecond( m );
        setMinute( s );
    } // end method set time



    public void setHour(int h)
    {
        if (h >= 0 && h < 24)
            hour = h;
        else
            throw new IllegalArgumentException("hour must be 0-23");
    } // end method  setHour



    // validate and set minute
    public void setMinute(int m)
    {
        if (m >= 0 && m < 60)
            minute = m;
        else
            throw new IllegalArgumentException("minute must be 0-59");
    }// end method setMinute


    // validate and set second
    public void setSecond(int s)
    {
        if (s >= 0 && s < 60)
            second = ( ( s >= 0 && s < 60 ) ? s: 0 );
        else
            throw new IllegalArgumentException("seconds must be 0-59");
    }  // end setSeconds


    // get value hour
    public int getHour()
    {
        return hour;
    }


    // get minute value
    public int getMinute()
    {
        return minute;
    }


    // get second Value
    public int getSecond()
    {
        return second;
    }

    // convert String in universal-time format (HH:MM::SS)
    public String toUniversalString()
    {
        return String.format("" +
                "%02d:%02d:%02d", getHour(),getMinute(), getSecond() );
    } // end method toUniversalString


    // convert to String in standard-time format (H:MM:SS AM or PM)
    public String toString()
    {
        return String.format("%d:%02d:%02d %s", ((getHour() == 0 || getHour() == 12) ? 12 : getHour() % 12),
                getMinute(), getSecond(), (getHour() < 12 ? "AM" : "PM") );
    } // end method toString


}
