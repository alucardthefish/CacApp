package com.sop.cacapp.Classes;

import android.content.Context;
import android.content.res.Resources;

import com.google.firebase.Timestamp;
import com.sop.cacapp.R;

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

    public OccurrenceDateTimeHandler(Context context) {
        this.context = context;
        this.occurrenceDate = new Date();
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

    public long getTimeDifferenceRespectToDate(Date date) {
        long timeDifference = this.occurrenceDate.getTime() - date.getTime();
        if (timeDifference < 0) {
            timeDifference *= -1;
        }
        return timeDifference;
    }

    public int getTimeElapsedInDaysToDate(Date date) {
        return (int) (getTimeDifferenceRespectToDate(date) / TimeUnits.DAY.getMillis());
    }

    public String getTimeElapsedByDate(Date date) {
        long time = getTimeDifferenceRespectToDate(date);
        return getTimeElapsedByDiffTime(time);
    }

    public String getTimeElapsed() {
        return getTimeElapsedByDate(new Date());
    }

    public String getTimeElapsedByDiffTime(long time) {
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

        Resources resources = context.getResources();

        if (diffInYears > 0 && units < 2) {
            String strUnitYear = (diffInYears > 1) ? resources.getString(R.string.plural_year) : resources.getString(R.string.singular_year);
            output += String.format(this.currentLocale, "%d %s ", diffInYears, strUnitYear);
            units++;
        }
        if (diffInMonths > 0 && units < 2) {
            String strUnitMonth = (diffInMonths > 1) ? resources.getString(R.string.plural_month) : resources.getString(R.string.singular_month);
            output += String.format(this.currentLocale, "%d %s ", concreteMonths, strUnitMonth);
            units++;
        }
        if (diffInDays > 0 && units < 2) {
            String strUnitDay = (diffInDays > 1) ? resources.getString(R.string.plural_day) : resources.getString(R.string.singular_day);
            output += String.format(this.currentLocale, "%d %s ", concreteDays, strUnitDay);
            units++;
        }
        if (diffInHours > 0 && units < 2) {
            String strUnitHour = (diffInHours > 1) ? resources.getString(R.string.plural_hour) : resources.getString(R.string.singular_hour);
            output += String.format(this.currentLocale, "%d %s ", concreteHours, strUnitHour);
            units++;
        }
        if (diffInMinutes > 0 && units < 2) {
            String strUnitMinute = (diffInMinutes > 1) ? resources.getString(R.string.plural_minute) : resources.getString(R.string.singular_minute);
            output += String.format(this.currentLocale, "%d %s ", concreteMinutes, strUnitMinute);
            units++;
        }
        if (diffInSeconds > 0 && units < 2) {
            String strUnitSecond = (diffInSeconds > 1) ? resources.getString(R.string.plural_second) : resources.getString(R.string.singular_second);
            output += String.format(this.currentLocale, "%d %s ", concreteMinutes, strUnitSecond);
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
