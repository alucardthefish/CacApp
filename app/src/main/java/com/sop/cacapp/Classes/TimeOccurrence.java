package com.sop.cacapp.Classes;

import com.google.firebase.Timestamp;

import java.util.Date;

public class TimeOccurrence {

    private Timestamp occurrenceTimestamp;

    public TimeOccurrence() {
        this.occurrenceTimestamp = new Timestamp(new Date());
    }

    public Timestamp getOccurrenceTimestamp() {
        return occurrenceTimestamp;
    }

    public Date getOccurrenceDate() {
        return this.occurrenceTimestamp.toDate();
    }
}
