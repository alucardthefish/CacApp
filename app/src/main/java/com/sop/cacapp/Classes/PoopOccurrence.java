package com.sop.cacapp.Classes;


import com.google.firebase.Timestamp;

import java.util.Date;

public class PoopOccurrence extends TimeOccurrence{

    private float satisfaction;

    public PoopOccurrence() {
        this.satisfaction = 3.0f;
    }

    public float getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(float satisfaction) {
        this.satisfaction = satisfaction;
    }
}
