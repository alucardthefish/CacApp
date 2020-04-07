package com.sop.cacapp.Classes;


import com.google.firebase.Timestamp;

import java.util.Date;

public class PoopOccurrence {

    private float satisfaction;
    private Timestamp occurrenceTime;

    public PoopOccurrence() {
        this.occurrenceTime = new Timestamp(new Date());
        this.satisfaction = 3.0f;
    }

    public float getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(float satisfaction) {
        this.satisfaction = satisfaction;
    }

    public Timestamp getOccurrenceTime() {
        return occurrenceTime;
    }
}
