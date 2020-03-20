package com.sop.cacapp.Classes;

import android.content.Context;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OccurrenceDateTimeHandler {

    private Date occurrenceDate;
    private Context context;
    private Locale currentLocale;

    public OccurrenceDateTimeHandler(Date occurrenceDate, Context context) {
        this.context = context;
        this.occurrenceDate = occurrenceDate;
        this.currentLocale = context.getResources().getConfiguration().locale;
    }

    public OccurrenceDateTimeHandler(Timestamp occurrenceTimestamp, Context context) {
        this.context = context;
        this.occurrenceDate = occurrenceTimestamp.toDate();
        this.currentLocale = context.getResources().getConfiguration().locale;
    }


    public String getFormattedOccurrenceDateTime(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, this.currentLocale);
        return simpleDateFormat.format(this.occurrenceDate);
    }

    public String getFullOccurrenceDateTime() {
        String pattern = "dd MMMM yyyy hh:mm aa";
        return getFormattedOccurrenceDateTime(pattern);
    }

    public long getTimeDifferenceRespectDate(Date date) {
        long timeDifference = this.occurrenceDate.getTime() - date.getTime();
        if (timeDifference < 0) {
            timeDifference *= -1;
        }
        return timeDifference;
    }

    public String getTimeElapsed(long time) {
        int units = 0;
        int diffInYears = (int) (time / TimeUnits.YEAR.getMillis());
        int diffInMonths = (int) (time / TimeUnits.MONTH.getMillis());
        int diffInWeeks = (int) (time / TimeUnits.WEEK.getMillis());
        int diffInDays = (int) (time / TimeUnits.DAY.getMillis());
        int diffInHours = (int) (time / TimeUnits.HOUR.getMillis());
        int diffInMinutes = (int) (time / TimeUnits.MINUTE.getMillis());
        int diffInSeconds = (int) (time / TimeUnits.SECOND.getMillis());

        int concreteMonths = diffInMonths - (diffInYears * 12);
        int concreteDays = diffInDays - (diffInMonths * 30);
        int concreteHours = diffInHours - (diffInDays * 24);
        int concreteMinutes = diffInMinutes - (diffInHours * 60);

        String output = "";

        if (diffInYears > 0 && units < 2) {
            output += String.format(this.currentLocale, "%d ano(s) ", diffInYears);
            units++;
        }
        if (diffInMonths > 0 && units < 2) {
            output += String.format(this.currentLocale, "%d mese(s) ", concreteMonths);
            units++;
        }
        if (diffInDays > 0 && units < 2) {
            output += String.format(this.currentLocale, "%d dia(s) ", concreteDays);
            units++;
        }
        if (diffInHours > 0 && units < 2) {
            output += String.format(this.currentLocale, "%d hora(s) ", concreteHours);
            units++;
        }
        if (diffInMinutes > 0 && units < 2) {
            output += String.format(this.currentLocale, "%d minuto(s) ", concreteMinutes);
            units++;
        }

        return output;
    }


    public enum TimeUnits {
        SECOND(1000L), MINUTE(60000L), HOUR(3600000L), DAY(86400000L), WEEK(604800000L), MONTH(2592000000L),
        YEAR(31104000000L);


        private long value;


        private TimeUnits (long val) {
            this.value = val;
        }

        public long getMillis() {
            return this.value;
        }

    }
}
