package com.sop.cacapp.Object;


import com.google.firebase.Timestamp;

import java.util.Date;

public class PoopOccurrence {

    private float satisfaction;
    private Timestamp occurrenceTime;

    public PoopOccurrence() {
        this.occurrenceTime = new Timestamp(new Date());
    }

    public PoopOccurrence(float satisfaction) {
        this.satisfaction = satisfaction;
        //this.occurrenceDateTime = LocalDateTime.now();
        this.occurrenceTime = new Timestamp(new Date());
    }

    public float getSatisfaction() {
        return satisfaction;
    }

    public Timestamp getOccurrenceTime() {
        return occurrenceTime;
    }
}
