package com.sop.cacapp.Classes;

public class HealthBackground {

    private String surgicalProcedures;
    private String alergies;
    private String conditionsOrIllnessesDiagnosed;
    private String familyBackground;

    public HealthBackground(){

    }

    public String getSurgicalProcedures() {
        return surgicalProcedures;
    }

    public void setSurgicalProcedures(String surgicalProcedures) {
        this.surgicalProcedures = surgicalProcedures;
    }

    public String getAlergies() {
        return alergies;
    }

    public void setAlergies(String alergies) {
        this.alergies = alergies;
    }

    public String getConditionsOrIllnessesDiagnosed() {
        return conditionsOrIllnessesDiagnosed;
    }

    public void setConditionsOrIllnessesDiagnosed(String conditionsOrIllnessesDiagnosed) {
        this.conditionsOrIllnessesDiagnosed = conditionsOrIllnessesDiagnosed;
    }

    public String getFamilyBackground() {
        return familyBackground;
    }

    public void setFamilyBackground(String familyBackground) {
        this.familyBackground = familyBackground;
    }
}
