public class Date {
    private int month;
    private int day;
    private int year;

    private static final int[] daysPerMonth = // days each month
            {0,31,28,31,30,31,30,31,31,30,31,30,31};

    public Date(int theMonth, int theDay, int theYear)
    {
        month = checkMonth(theMonth);
        year = theYear;
        day = checkDay(theDay);

        System.out.printf("Date object constructor for date %s\n", this);
    }



    private int checkMonth(int testMonth)
    {
        if (testMonth > 0 && testMonth <= 12)
            return testMonth;
        else
            throw new IllegalArgumentException("month must be 1-12");

    } // end method checkMonth

    private int checkDay( int testDay )
    {
        // check if day in range for month
        if (testDay > 0 && testDay <= daysPerMonth [ month ] )
            return testDay;

        // check year leap
        if (month == 2 && testDay == 29 && (year % 400 == 0 ||
                (year % 4 == 0 && year % 100 != 0)))
            return testDay;

        throw new IllegalArgumentException("day out-of-range for specified month and year");

    } // end method check day


    public String toString()
    {
        return String.format("%d/%d%d", month, day, year);

    } // end method toString
}
