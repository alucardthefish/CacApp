package com.sop.cacapp.Classes;

public class Symptom extends TimeOccurrence {

    private String description;
    private float intensity;

    public Symptom() {
        this.description = "";
        this.intensity = 3.0f;
    }

    public Symptom(String description, float intensity) {
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
