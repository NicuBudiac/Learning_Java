package com;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public class GetTime{
        long Time;
        // Function to print difference in
        // time start_date and end_date
        public Long findDifference(String start_date,
                                   String end_date)
        {
            // SimpleDateFormat converts the
            // string format to date object
            SimpleDateFormat sdf
                    = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss");

            // Try Class
            try {

                // parse method is used to parse
                // the text from a string to
                // produce the date
                Date d1 = sdf.parse(start_date);
                Date d2 = sdf.parse(end_date);

                // Calucalte time difference
                // in milliseconds
                long difference_In_Time
                        = d2.getTime() - d1.getTime();

                // Calucalte time difference in seconds,
                // minutes, hours, years, and days
                long difference_In_Seconds
                        = TimeUnit.MILLISECONDS
                        .toSeconds(difference_In_Time);
                Time = difference_In_Seconds;


            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            return Time;
        }



}
