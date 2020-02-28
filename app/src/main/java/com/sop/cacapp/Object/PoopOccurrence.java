package com.sop.cacapp.Object;


import com.google.firebase.Timestamp;

import java.util.Date;

public class PoopOccurrence {

    private String status;
    private Timestamp occurrenceTime;

    public PoopOccurrence() {
        this.status = "nuevo";
        //this.occurrenceDateTime = LocalDateTime.now();
        this.occurrenceTime = new Timestamp(new Date());
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getOccurrenceTime() {
        return occurrenceTime;
    }
}
