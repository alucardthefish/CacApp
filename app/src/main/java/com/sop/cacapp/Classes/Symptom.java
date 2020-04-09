package com.sop.cacapp.Classes;

import java.util.UUID;

public class Symptom extends TimeOccurrence {

    private String id;
    private String description;
    private float intensity;

    public Symptom() {
        this.id = UUID.randomUUID().toString();
        this.description = "";
        this.intensity = 3.0f;
    }

    public Symptom(String description, float intensity) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.intensity = intensity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getIntensity() {
        return intensity;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        String occurrence = super.getOccurrenceTimestamp().toString();
        return "Symptom{" +
                "description='" + description + '\'' +
                ", intensity=" + intensity +
                ", datetime='" + occurrence + '\'' +
                '}';
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
